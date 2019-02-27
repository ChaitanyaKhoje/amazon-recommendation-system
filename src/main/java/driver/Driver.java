package driver;

import recommender.Handler;
import recommender.Recommender;
import recommender.SentimentAnalyzer;
import util.FileProcessor;

public class Driver {

    /**
     * The main method; the Driver.
     * @param args
     */
    public static void main(String[] args) {

        if (validate(args)) {
            startSentimentAnalyzer();   // Process holds until complete, need to change later.
            populateInMemory(args[0]);
            startRecommender();
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
     * Initializes the Scanner with the given file, passes the FileProcessor to a handler class who populates
     * the data in-memory.
     * @param filePath
     */
    public static void populateInMemory(String filePath) {

        Handler handler = initialize(filePath);
        handler.populateInMemory();
    }

    /**
     * Initializes FileProcessor and the Handler class.
     * @param filePath
     * @return
     */
    public static Handler initialize(String filePath) {

        FileProcessor fileProcessor = new FileProcessor(filePath);
        return new Handler(fileProcessor);
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
     */
    public static void startRecommender() {

        Recommender recommender = new Recommender();
        recommender.start();
    }
}
