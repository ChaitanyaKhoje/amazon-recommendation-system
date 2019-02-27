package driver;

import recommender.Handler;
import recommender.Recommender;
import recommender.SentimentAnalyzer;

public class Driver {

    /**
     * The main method; the Driver.
     * @param args
     */
    public static void main(String[] args) {

        if (validate(args)) {
            startSentimentAnalyzer();   // Process holds until operation complete, need to change later.
            Handler handler = initialize(args[0]);
            startRecommender(handler);
        }
    }

    /**
     * Validates program arguments
     * @param args
     * @return boolean
     */
    public static boolean validate(String[] args) {

        if (args.length == 1) {
            return true;
        }
        return false;
    }

    /**
     * Initializes FileProcessor and the Handler class.
     * @param filePath
     * @return
     */
    public static Handler initialize(String filePath) {

        Handler handler = new Handler();
        handler.processData(filePath);
        return handler;
    }

    /**
     * Performs sentiment analysis.
     */
    public static void startSentimentAnalyzer() {

        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        sentimentAnalyzer.performSentimentAnalysis();
    }

    /**
     * Starts the recommendation engine
     * The Handler instance holds all the data in-memory that we need for the recommendation system.
     */
    public static void startRecommender(Handler handler) {

        Recommender recommender = new Recommender(handler);
        recommender.start();
    }
}
