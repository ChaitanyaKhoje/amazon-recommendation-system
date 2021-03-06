package recommender;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class Review {

    private String reviewerID = "";
    private String asin = "";
    private String reviewerName = "";
    private int[] helpful;
    private String reviewText = "";
    private int overall;
    private String summary = "";
    private long unixReviewTime;
    private String reviewTime;
    private int sentiment = 0;

    public Review() { }

    public Review(String line) {
        JSONObject jsonObject = new JSONObject(line);
        if (!jsonObject.isEmpty()) {
            if (jsonObject.has("helpful")) {
                JSONArray helpfulJSONArr = jsonObject.getJSONArray("helpful");
                int[] helpful = new int[helpfulJSONArr.length()];
                for (int i = 0; i < helpfulJSONArr.length(); i++) {
                    helpful[i] = helpfulJSONArr.optInt(i);
                }
                setHelpful(helpful);
            } else {
                setHelpful(new int[2]);
            }
            if (jsonObject.has("overall")) {
                setOverall(jsonObject.getInt("overall"));
            } else {
                setOverall(0);
            }
            if (jsonObject.has("asin")) {
                String asin = jsonObject.getString("asin");
                setAsin("\'"+asin+"\'");
            } else {
                setAsin("");
            }
            if (jsonObject.has("reviewerID")) {
                setReviewerID(jsonObject.getString("reviewerID"));
            } else {
                setReviewerID("");
            }
            if (jsonObject.has("reviewText")) {
                setReviewText(jsonObject.getString("reviewText"));
            } else {
                setReviewText("");
            }
            if (jsonObject.has("reviewerName")) {
                setReviewerName(jsonObject.getString("reviewerName"));
            } else {
                setReviewerName("");
            }
            if (jsonObject.has("unixReviewTime")) {
                setUnixReviewTime(jsonObject.getLong("unixReviewTime"));
            } else {
                setUnixReviewTime(0);
            }
            if (jsonObject.has("reviewTime")) {
                String time = jsonObject.getString("reviewTime");
                setReviewTime("\'"+time+"\'");
            } else {
                setReviewTime("");
            }
            if (jsonObject.has("summary")) {
                setSummary(jsonObject.getString("summary"));
            } else {
                setSummary("");
            }
            if (jsonObject.has("sentiment")) {
                setSentiment(jsonObject.getInt("sentiment"));
            } else {
                setSentiment(0);
            }
        }
    }

    public JSONObject getJSONObjectForProduct(Review line) {

        JSONObject jsonObject = new JSONObject(line);
        if (!jsonObject.isEmpty()) {
            /*if (jsonObject.getString("asin").equals("1933622709")) {
                System.out.println("");
            }*/

            if (jsonObject.has("helpful")) {
                JSONArray helpfulJSONArr = jsonObject.getJSONArray("helpful");
                int[] helpful = new int[helpfulJSONArr.length()];
                for (int i = 0; i < helpfulJSONArr.length(); i++) {
                    helpful[i] = helpfulJSONArr.optInt(i);
                }
                setHelpful(helpful);
            } else {
                setHelpful(new int[2]);
            }
            if (jsonObject.has("overall")) {
                setOverall(jsonObject.getInt("overall"));
            } else {
                setOverall(0);
            }
            if (jsonObject.has("asin")) {
                setAsin(jsonObject.getString("asin"));
            } else {
                setAsin("");
            }
            if (jsonObject.has("reviewerID")) {
                setReviewerID(jsonObject.getString("reviewerID"));
            } else {
                setReviewerID("");
            }
            if (jsonObject.has("reviewText")) {
                setReviewText(jsonObject.getString("reviewText"));
            } else {
                setReviewText("");
            }
            if (jsonObject.has("reviewerName")) {
                setReviewerName(jsonObject.getString("reviewerName"));
            } else {
                setReviewerName("");
            }
            if (jsonObject.has("unixReviewTime")) {
                setUnixReviewTime(jsonObject.getLong("unixReviewTime"));
            } else {
                setUnixReviewTime(0);
            }
            if (jsonObject.has("summary")) {
                setSummary(jsonObject.getString("summary"));
            } else {
                setSummary("");
            }
            if (jsonObject.has("sentiment")) {
                setSentiment(jsonObject.getInt("sentiment"));
            } else {
                setSentiment(0);
            }
        }
        return jsonObject;
    }

    public String getReviewerID() {
        return reviewerID;
    }

    public void setReviewerID(String reviewerID) {
        this.reviewerID = reviewerID;
    }

    public String getAsin() {
        return asin.replace("'", "");
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public int[] getHelpful() {
        return helpful;
    }

    public void setHelpful(int[] helpful) {
        this.helpful = helpful;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getOverall() {
        return overall;
    }

    public void setOverall(int overall) {
        this.overall = overall;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public double getUnixReviewTime() {
        return unixReviewTime;
    }

    public void setUnixReviewTime(long unixReviewTime) {
        this.unixReviewTime = unixReviewTime;
    }

    public int getSentiment() {
        return sentiment;
    }

    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }

    public String getReviewTime() {
        return reviewTime.replace("'", "");
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewerID='" + reviewerID + '\'' +
                ", asin=" + asin +
                ", reviewerName='" + reviewerName + '\'' +
                ", helpful=" + Arrays.toString(helpful) +
                ", reviewText='" + reviewText + '\'' +
                ", overall=" + overall +
                ", summary='" + summary + '\'' +
                ", unixReviewTime=" + unixReviewTime +
                ", reviewTime='" + reviewTime + '\'' +
                ", sentiment=" + sentiment +
                '}';
    }
}


