package recommender;

import java.util.Arrays;

public class Product {

    private String reviewerID = "";
    private String productID = "";
    private String reviewerName = "";
    private int[] helpfulness;
    private String reviewText = "";
    private int overallRating;
    private String summary = "";
    private String reviewTime;
    private int sentiment = 0;

    public Product() { }

    public String getReviewerID() {
        return reviewerID;
    }

    public void setReviewerID(String reviewerID) {
        this.reviewerID = reviewerID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public int[] getHelpfulness() {
        return helpfulness;
    }

    public void setHelpfulness(int[] helpfulness) {
        this.helpfulness = helpfulness;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(int overallRating) {
        this.overallRating = overallRating;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public int getSentiment() {
        return sentiment;
    }

    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }

    @Override
    public String toString() {
        return "Product{" +
                "reviewerID='" + reviewerID + '\'' +
                ", productID='" + productID + '\'' +
                ", reviewerName='" + reviewerName + '\'' +
                ", helpfulness=" + Arrays.toString(helpfulness) +
                ", reviewText='" + reviewText + '\'' +
                ", overallRating=" + overallRating +
                ", summary='" + summary + '\'' +
                ", reviewTime='" + reviewTime + '\'' +
                ", sentiment=" + sentiment +
                '}';
    }
}


