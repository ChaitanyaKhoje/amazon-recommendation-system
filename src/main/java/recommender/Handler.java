package recommender;

import org.json.JSONArray;
import org.json.JSONObject;
import util.FileProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Handler {

    private FileProcessor fileProcessor = null;

    // Holds the input data read from input.txt
    private Map<String, List<String>> input = new HashMap<String, List<String>>();
    // All products
    private Set<Product> products = new HashSet<Product>();
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

    public Handler() { }

    /**
     * Iterates over all the files residing in a given directory
     * @param dirPath
     */
    public void processData(String dirPath, String inputFPath) {

        if (inputFPath != null && !inputFPath.isEmpty()) {
            File inputFile = new File(inputFPath);
            if (inputFile.getName().endsWith(".json")) {
                FileProcessor fp = new FileProcessor(inputFile.getPath());
                while (fp.hasNextLine()) {
                    String line = fp.getNextLine();
                    parseInputData(line);
                }
            } else {
                System.err.println("The input file should be a .json!\nPlease supply appropriate file and rerun.");
                System.exit(-1);
            }
        }

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

            // DEBUG
            if (jsonObject.getString("reviewerID").equals("A1L5P841VIO02V")) {
                System.out.println();
            }
            mapDetails(jsonObject, product);     // Store all the required mapping for recommendation
        }
    }

    /**
     * Parses the input.json file for a reviewer and the products he bought.
     * @param line
     */
    public void parseInputData(String line) {

        if (line != null && !line.isEmpty()) {
            String rID = "";
            List<String> boughtProducts = new ArrayList<String>();
            JSONObject jsonObject = new JSONObject(line);
            if (!jsonObject.isEmpty()) {
                if (jsonObject.has("reviewerID")) {
                    rID = jsonObject.getString("reviewerID");
                }
                if (jsonObject.has("products_bought")) {
                    JSONArray productsJObs = jsonObject.getJSONArray("products_bought");
                    for (int i = 0; i < productsJObs.length(); i++) {
                        boughtProducts.add(productsJObs.optString(i));
                    }
                }
                input.put(rID, boughtProducts);
            }
        }
    }

    /**
     * Fills required data into different sets and maps
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
