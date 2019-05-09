package driver;

import recommender.Handler;
import recommender.Recommender;

import java.util.Scanner;

public class Driver {

    /**
     * The main method; the Driver.
     * @param args
     */
    public static void main(String[] args) {

        if (validate(args)) {
            // PROGRAM ARGUMENTS:
            // 1st argument is the dataset/JSON files
            // Handler object is to be used while producing and while recommending, hence declared at class level as static
            initialize(args[0]);
            getUserInput();
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
                    startRecommender(false);
                    break;
                case 2:
                    startRecommender(true);
                    break;
                case 3:
                    shutdown();
                    break;
            }
        }
    }

    /**
     * Initializes FileProcessor and the Handler class.
     * @param dirPath
     * @return
     */
    public static void initialize(String dirPath) {

        Handler handler = new Handler();
        handler.produceData(dirPath);
    }

    public static void shutdown() {

        System.out.println("Shutting down...");

    }

    /**
     * Starts the recommendation engine
     * The Handler instance holds all the data in-memory that we need for the recommendation system.
     */
    public static void startRecommender(boolean sentimentAnalysis) {

        Recommender recommender = new Recommender();
        recommender.start(sentimentAnalysis);
    }
}
