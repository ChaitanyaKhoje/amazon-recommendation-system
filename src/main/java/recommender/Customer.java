package recommender;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private String reviewerID = "";
    private String reviewerName = "";
    private List<Product> products = new ArrayList<Product>();

    public Customer(String id, String name, List<Product> prods) {

        reviewerID = id;
        reviewerName = name;
        products = prods;
    }

    public String getReviewerID() {
        return reviewerID;
    }

    public void setReviewerID(String reviewerID) {
        this.reviewerID = reviewerID;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
