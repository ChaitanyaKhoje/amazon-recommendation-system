package recommender;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.json.JSONArray;
import org.json.JSONObject;
import util.Constants;
import util.FileProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Handler {

    private FileProcessor fileProcessor = null;

    // Holds the input data read from input.json
    private Map<String, List<String>> input = new HashMap<String, List<String>>();
    // All productSet
    private Set<String> productSet = new HashSet<String>();
    // All users by their review IDs (name, ID)
    private Map<String, String> users = new HashMap<String, String>();

    // All reviews
    private Set<Review> reviewSet = new HashSet<Review>();

    // To fetch list of products for a particular user
    // Map --> <User, Products>
    private Map<String, Set<String>> userToProductMap = new HashMap<String, Set<String>>();

    // To fetch list of users (reviewerIDs) for a particular product
    // Map --> <Review, Users>
    private Map<String, Set<String>> productToUserMap = new HashMap<String, Set<String>>();

    private Map<String, Customer> customers = new HashMap<>();
    private Map<String, Product> products = new HashMap<>();

    public Handler() { }

    /**
     * Populates in-memory module
     *
     * @param records
     */
    public void populateInMemory(ConsumerRecords<Long, Review> records) {

        for (ConsumerRecord<Long, Review> record : records) {
            parseProductData(record.value());
        }
    }

    /**
     * Add the productSet that were consumed.
     *
     * @param review
     */
    public void parseProductData(Review review) {

        productSet.add(review.getAsin());
        users.put(review.getReviewerName(), review.getReviewerID());
        // Update customer and product data by reading the reviews
        updateCustomers(review);
        updateProducts(review);
    }

    /**
     *  Creates a customer object and updates it
     * @param review
     */
    public void updateCustomers(Review review) {

        String id = review.getReviewerID();
        String name = review.getReviewerName();
        String asin = review.getAsin();
        Product product = new Product(asin);
        // Do we have records for this customer?
        Customer oldCustomer = customers.get(id);
        // if yes, fetch his products and add our new product to the list
        List<Product> newList = oldCustomer == null ? new ArrayList<>() : oldCustomer.getProducts();
        newList.add(product);
        // Now construct our new customer
        Customer customer = new Customer(id, name, newList);
        // Check if our customers list already has this one, if not.. add him/her.
        if (!customers.containsKey(id)) customers.put(id, customer);
    }

    /**
     * Creates a product object and updates it
     * @param review
     */
    public void updateProducts(Review review) {

        String asin = review.getAsin();
        Product product = new Product(asin);
        products.put(asin, product);
    }

    /**
     * Parses the input.json file for a reviewer and the productSet he bought.
     */
    public void parseInputData() {

        String path = System.getProperty("user.dir") + "/data/input/input.json";
        FileProcessor inputFP = new FileProcessor(path);
        while (inputFP.hasNextLine()) {
            String line = inputFP.getNextLine();
            if (line != null && !line.equals("")) {
                JSONObject j = new JSONObject(line);
                String customerName = j.getString("customer");
                String productBought = j.getString("product");
                List<String> tempProducts = input.get(customerName) == null ? new ArrayList<>() : input.get(customerName);
                tempProducts.add(productBought);
                input.put(customerName, tempProducts);
            }
        }
    }

    /**
     * Fills required data into different sets and maps
     *
     * @param response
     */
    public void mapDetails(String response) {

        JSONArray jsonArray = new JSONArray(response);

        // {"summary":"Best Price"
        // ,"sentiment":1
        // ,"reviewerName":"A"
        // ,"reviewerID":"APYOBQE6M18AA"
        // ,"overall":5
        // ,"asin":"'1'"
        // ,"unixReviewTime":1.3821408E9
        // ,"helpful":[0,0]
        // ,"reviewText":"My daughter ... with it."
        // ,"reviewTime":"'10 19, 2013'"}

        for(int i = 0; i < jsonArray.length(); i++) {
            // User to products mapping
            JSONObject j = (JSONObject) jsonArray.get(i);
            // Populate reviewSet, which will be used later on for checking sentiments while recommending
            Review review = new Review(j.toString());
            reviewSet.add(review);
            Set<String> userProducts = userToProductMap.get(j.getString("reviewerID"));
            if (userProducts == null) userProducts = new HashSet<String>();
            if (!userProducts.isEmpty()) {
                //TODO If there are productSet assigned to this user, check if current review already exists

            }
            userProducts.add(j.getString("asin"));

            if (j.has("reviewerID")) {
                // Map reviewer ID to all the productSet he reviewed.
                userToProductMap.put(j.getString("reviewerID"), userProducts);
            }


        }

        // Product to users mapping
        // productSet contains the names of existing products only.
        for (String product: productSet) {
            // user to products map contains info about which user bought what products
            for (Map.Entry entry: userToProductMap.entrySet()) {
                // We fetch those products for each user and check if our product matches with this entry
                Set prods = (Set) entry.getValue();
                // if it matches, which means we got a user for our product, add the user to our productToUserMap map
                if (prods.contains(product)) {
                    // Store key i.e the user for this product
                    Set<String> productUsers =
                            productToUserMap.get(product) == null ?
                                    new HashSet<>() : productToUserMap.get(product);
                    // Below, the entry.getKey() gives us the user from userToProductMap
                    productUsers.add((String) entry.getKey());
                    productToUserMap.put(product, productUsers);
                }
            }
        }
    }

    /**
     * The entry point of the program:
     * Initializes the kafka producer.
     */
    public void produceData(String dirPath) {

        // Start zookeeper service and the kafka server before this.
        // startServices(kafkaPath);
        // Create the producer
        final Producer<String, Review> producer = ProducerCreator.createProducer();
        ProducerHandler producerHandler = new ProducerHandler(dirPath, producer, Constants.TOPIC_NAME);
        Thread producerThread = new Thread(producerHandler);
        System.out.println("DEBUG: Starting producer thread!");
        // Start producing messages from in a new thread
        producerThread.start();
    }

    public void startServices(String kafkaPath) {


        String[] cmd = {"bash", "-c", "kafka_2.12-2.2.0/kafka_start.sh amazondata"};
        try {
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec(cmd);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));

// read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

// read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getProductSet() {
        return productSet;
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public FileProcessor getFileProcessor() {
        return fileProcessor;
    }

    public void setFileProcessor(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }

    public Set<Review> getReviewSet() {
        return reviewSet;
    }

    public Map<String, Set<String>> getUserToProductMap() {
        return userToProductMap;
    }

    public Map<String, Set<String>> getProductToUserMap() {
        return productToUserMap;
    }

    public Map<String, List<String>> getInput() {
        return input;
    }

    @Override
    public String toString() {
        return "Handler{" +
                "fileProcessor=" + fileProcessor +
                ", input=" + input +
                ", productSet=" + productSet +
                ", users=" + users +
                ", reviewSet=" + reviewSet +
                ", userToProductMap=" + userToProductMap +
                ", productToUserMap=" + productToUserMap +
                '}';
    }
}
