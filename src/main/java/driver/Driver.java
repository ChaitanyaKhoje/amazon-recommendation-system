package driver;

import recommender.Handler;
import recommender.Recommender;
import recommender.SentimentAnalyzer;

import java.util.Scanner;

public class Driver {

    private Handler handler = new Handler();
    /**
     * The main method; the Driver.
     * @param args
     */
    public static void main(String[] args) {

        if (validate(args)) {
            // PROGRAM ARGUMENTS:
            // 1st argument is the dataset/JSON files
            // 2nd argument is the KAFKA folder path (to start zookeeper and kafka)
            // 3rd argument is a thread sleep time in seconds. (interval for kafka producer)

            Handler handler = new Handler();
            initialize(args[0], args[1], Integer.parseInt(args[2]), handler);
            getUserInput();

            /*startSentimentAnalyzer();   // Process holds until operation complete, need to change later.
            Handler handler = initialize(args[0], args[1]);
            startRecommender(handler);*/
        }
    }

    /**
     * Validates program arguments
     * @param args
     * @return boolean
     */
    public static boolean validate(String[] args) {

        if (args.length == 3) {
            return true;
        } else {
            System.err.println("Invalid number of arguments passed!\nPlease pass in the data directory-path & the input file path");
            return false;
        }
    }

    public static void getUserInput() {

        while(true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("--- RECOMMENDATION SYSTEM ---");
            System.out.println("usage: <operation_number> \n");
            System.out.println("1. Get recommendations WITHOUT sentiment analysis");
            System.out.println("2. Get recommendations WITH sentiment analysis");
            System.out.println("3. Exit program.");
            int input = sc.nextInt();
            switch (input) {
                case 1:

                    break;
                case 2:
                    break;
                case 3:

                    break;
            }
        }
    }

    /**
     * Initializes FileProcessor and the Handler class.
     * @param dirPath
     * @return
     */
    public static void initialize(String dirPath, String kafkaPath, int timeToSleep, Handler handler) {

        handler.produceData(dirPath, kafkaPath, timeToSleep);
    }

    public static void shutdown() {


    }

    /**
     * Initializes Kafka producer(s)
     */
    public static void produce(String kafkaPath) {


    }

    /**
     * Performs sentiment analysis.
     */
    public static void startSentimentAnalyzer() {

        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        sentimentAnalyzer.performSentimentAnalysis();
        System.out.println("");
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
