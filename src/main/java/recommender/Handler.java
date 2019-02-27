package recommender;

import org.json.JSONArray;
import org.json.JSONObject;
import util.FileProcessor;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Handler {

    private FileProcessor fileProcessor = null;
    // All products
    private Set<Product> products = new HashSet<Product>();
    // All users by their review IDs
    private Set<String> users = new HashSet<String>();

    // To fetch a single product by its "asin" and all its attributes.
    private Map<String, Product> productsMap = new HashMap<String, Product>();

    // To fetch list of products for a particular user
    // Map --> <User, Products>
    private Map<String, Set<Product>> userToProductMap = new HashMap<String, Set<Product>>();

    // To fetch list of users (reviewerIDs) for a particular product
    // Map --> <Product, Users>
    private Map<String, Set<String>> productToUserMap = new HashMap<String, Set<String>>();

    public Handler() { }

    /**
     * Iterates over all the files residing in a given directory
     * @param dirPath
     */
    public void processData(String dirPath) {

        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".json")) {
                        setFileProcessor(new FileProcessor(file.getPath()));
                        populateInMemory();
                    }
                }
            }
        }
    }

    /**
     * Populates in-memory module
     */
    public void populateInMemory() {

        if (fileProcessor != null) {
            while (fileProcessor.hasNextLine()) {
                // Build Product object --
                String line = fileProcessor.getNextLine();
                if (line != null && !line.isEmpty()) {
                    System.out.println(line);
                    parseProductData(line);
                } else {
                    System.out.println("DEBUG: Line empty while parsing!");
                }
            }
        }
    }

    /**
     * Parses input line
     * @param line
     */
    public void parseProductData(String line) {

        JSONObject jsonObject = new JSONObject(line);
        if (!jsonObject.isEmpty()) {
            if (jsonObject.getString("asin").equals("1933622709")) {
                System.out.println("");
            }
            Product product = new Product();
            if (jsonObject.has("helpful")) {
                JSONArray helpfulJSONArr = jsonObject.getJSONArray("helpful");
                int[] helpful = new int[helpfulJSONArr.length()];
                for (int i = 0; i < helpfulJSONArr.length(); i++) {
                    helpful[i] = helpfulJSONArr.optInt(i);
                }
                product.setHelpfulness(helpful);
            } else {
                product.setHelpfulness(new int[2]);
            }
            if (jsonObject.has("overall")) {
                product.setOverallRating(jsonObject.getInt("overall"));
            } else {
                product.setOverallRating(0);
            }
            if (jsonObject.has("asin")) {
                product.setProductID(jsonObject.getString("asin"));
            } else {
                product.setProductID("");
            }
            if (jsonObject.has("reviewerID")) {
                product.setReviewerID(jsonObject.getString("reviewerID"));
            } else {
                product.setReviewerID("");
            }
            if (jsonObject.has("reviewText")) {
                product.setReviewText(jsonObject.getString("reviewText"));
            } else {
                product.setReviewText("");
            }
            if (jsonObject.has("reviewerName")) {
                product.setReviewerName(jsonObject.getString("reviewerName"));
            } else {
                product.setReviewerName("");
            }
            if (jsonObject.has("reviewTime")) {
                product.setReviewTime(jsonObject.getString("reviewTime"));
            } else {
                product.setReviewTime("");
            }
            if (jsonObject.has("summary")) {
                product.setSummary(jsonObject.getString("summary"));
            } else {
                product.setSummary("");
            }
            if (jsonObject.has("sentiment")) {
                product.setSentiment(jsonObject.getInt("sentiment"));
            } else {
                product.setSentiment(0);
            }

            products.add(product);
            users.add(product.getReviewerID());

            if (jsonObject.has("asin")) productsMap.put(jsonObject.getString("asin"), product);

            if (jsonObject.getString("reviewerID").equals("A1L5P841VIO02V")) {
                System.out.println();
            }
            mapDetails(jsonObject, product);     // Store all the required mapping for recommendation
        }
    }

    /**
     * Fills required data into different sets and maps
     * @param jsonObject
     * @param product
     */
    public void mapDetails(JSONObject jsonObject, Product product) {

        // User to products mapping
        Set<Product> userProducts = userToProductMap.get(jsonObject.getString("reviewerID"));
        if (userProducts == null) userProducts = new HashSet<Product>();
        if (!userProducts.isEmpty()) {
            //TODO If there are products assigned to this user, check if current product already exists

        }
        userProducts.add(product);

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
        productToUserMap.put(product.getProductID(), productUsers);
    }

    public Set<Product> getProducts() {
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

    public Map<String, Set<Product>> getUserToProductMap() {
        return userToProductMap;
    }

    public Map<String, Set<String>> getProductToUserMap() {
        return productToUserMap;
    }
}
