package recommender;

import org.json.*;
import util.FileProcessor;

import java.util.ArrayList;
import java.util.List;


public class Handler {

    private FileProcessor fileProcessor = null;
    private List<Product> products = new ArrayList<Product>();

    public Handler(FileProcessor fp) {
        fileProcessor = fp;
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
        Product product = new Product();
        JSONArray helpfulJSONArr = jsonObject.getJSONArray("helpful");
        int[] helpful = new int[helpfulJSONArr.length()];
        for (int i = 0; i < helpfulJSONArr.length(); i++) {
            helpful[i] = helpfulJSONArr.optInt(i);
        }
        product.setHelpfulness(helpful);
        product.setOverallRating(jsonObject.getInt("overall"));
        product.setProductID(jsonObject.getString("asin"));
        product.setReviewerID(jsonObject.getString("reviewerID"));
        product.setReviewText(jsonObject.getString("reviewText"));
        product.setReviewerName(jsonObject.getString("reviewerName"));
        product.setReviewTime(jsonObject.getString("reviewTime"));
        product.setSummary(jsonObject.getString("summary"));

        products.add(product);
    }

    public List<Product> getProducts() {
        return products;
    }
}
