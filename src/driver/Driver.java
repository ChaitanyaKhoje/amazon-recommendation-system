package driver;

import recommender.Handler;
import util.FileProcessor;

public class Driver {

    public static void main(String[] args) {

        if (validate(args)) {
            initialize(args[0]);

        }
    }

    public static boolean validate(String[] args) {

        if (args.length == 1) {
            return true;
        }
        return false;
    }

    /**
     * Initializes the scanner with the given file, passes the FileProcessor to a handler class who populates the data
     * in-memory.
     * @param filePath
     */
    public static void initialize(String filePath) {

        FileProcessor fileProcessor = new FileProcessor(filePath);
        Handler handler = new Handler(fileProcessor);
        handler.populateInMemory();
    }

    public static void startRecommender() {

    }
}
