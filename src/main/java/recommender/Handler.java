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
    // All products
    private Set<String> products = new HashSet<String>();
    // All users by their review IDs
    private Set<String> users = new HashSet<String>();

    // To fetch a single product by its "asin" and all its attributes.
    private Map<String, Product> productsMap = new HashMap<String, Product>();

    // To fetch list of products for a particular user
    // Map --> <User, Products>
    private Map<String, Set<String>> userToProductMap = new HashMap<String, Set<String>>();

    // To fetch list of users (reviewerIDs) for a particular product
    // Map --> <Product, Users>
    private Map<String, Set<String>> productToUserMap = new HashMap<String, Set<String>>();

    private List<Customer> customers = new ArrayList<Customer>();

    public Handler() { }

    /**
     * Populates in-memory module
     *
     * @param records
     */
    public void populateInMemory(ConsumerRecords<Long, Product> records) {

        for (ConsumerRecord<Long, Product> record : records) {
            parseProductData(record.value());
        }
    }

    /**
     * Add the products that were consumed.
     *
     * @param product
     */
    public void parseProductData(Product product) {

        products.add(product.getAsin());
        users.add(product.getReviewerID());
        // Update customer data


    }

    public void updateSentiments(String response) {

        JSONArray jsonArray = new JSONArray(response);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            System.out.println(jsonObject);
            // Update sentiment of the product by user level
        }
    }

    /**
     * Parses the input.json file for a reviewer and the products he bought.
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
     * @param jsonObject
     * @param product
     */
    public void mapDetails(JSONObject jsonObject, Product product) {

        // User to products mapping
        Set<String> userProducts = userToProductMap.get(jsonObject.getString("reviewerID"));
        if (userProducts == null) userProducts = new HashSet<String>();
        if (!userProducts.isEmpty()) {
            //TODO If there are products assigned to this user, check if current product already exists

        }
        userProducts.add(jsonObject.getString("asin"));

        if (jsonObject.has("reviewerID")) {
            // Map reviewer ID to all the products he reviewed.
            userToProductMap.put(jsonObject.getString("reviewerID"), userProducts);
        }

        // Product to users mapping
        Set<String> productUsers = productToUserMap.get(jsonObject.getString("asin"));
        if (productUsers == null) productUsers = new HashSet<String>();
        if (!productUsers.isEmpty()) {
            //TODO Do something
        }

        productUsers.add(jsonObject.getString("reviewerID"));
        productToUserMap.put(product.getAsin(), productUsers);
    }

    /**
     * The entry point of the program:
     * Initializes the kafka producer.
     */
    public void produceData(String dirPath) {

        // Start zookeeper service and the kafka server before this.
        // startServices(kafkaPath);
        // Create the producer
        final Producer<String, Product> producer = ProducerCreator.createProducer();
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

    public Set<String> getProducts() {
        return products;
    }

    public Set<String> getUsers() {
        return users;
    }

    public FileProcessor getFileProcessor() {
        return fileProcessor;
    }

    public void setFileProcessor(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }

    public Map<String, Product> getProductsMap() {
        return productsMap;
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
                ", products=" + products +
                ", users=" + users +
                ", productsMap=" + productsMap +
                ", userToProductMap=" + userToProductMap +
                ", productToUserMap=" + productToUserMap +
                '}';
    }
}
