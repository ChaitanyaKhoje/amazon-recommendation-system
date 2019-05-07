package driver;

import recommender.Handler;
import recommender.Recommender;

import java.util.Scanner;

public class Driver {

    private final static Handler handler = new Handler();
    /**
     * The main method; the Driver.
     * @param args
     */
    public static void main(String[] args) {

        if (validate(args)) {
            // PROGRAM ARGUMENTS:
            // 1st argument is the dataset/JSON files
            // 2nd argument is the KAFKA folder path (to start zookeeper and kafka)
            // 3rd argument is the ip address of the consumer server
            // 4th argument is the port number of the consumer server
            // Handler object is to be used while producing and while recommending, hence declared at class level as static
            initialize(args[0], args[1], handler);
            getUserInput(args);
        }
    }

    /**
     * Validates program arguments
     * @param args
     * @return boolean
     */
    public static boolean validate(String[] args) {

        if (args.length == 4) {
            return true;
        } else {
            System.err.println("Invalid number of arguments passed!\nPlease pass in the data directory-path & the input file path");
            return false;
        }
    }

    public static void getUserInput(String[] args) {

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
                    startRecommender(handler, args, false);
                    break;
                case 2:
                    startRecommender(handler, args, true);
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
    public static void initialize(String dirPath, String kafkaPath, Handler handler) {

        handler.produceData(dirPath, kafkaPath);
    }

    public static void shutdown() {

        System.out.println("Shutting down...");

    }

    /**
     * Starts the recommendation engine
     * The Handler instance holds all the data in-memory that we need for the recommendation system.
     */
    public static void startRecommender(Handler handler, String[] args, boolean sentimentAnalysis) {

        Recommender recommender = new Recommender(handler);
        recommender.start(args, sentimentAnalysis);
    }
}
