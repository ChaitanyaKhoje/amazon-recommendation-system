package driver;

import util.FileProcessor;

public class Driver {

    public static void main(String[] args) {

        if (validate(args)) {
            readData(args[0]);
        }
    }

    public static boolean validate(String[] args) {

        if (args.length == 1) {
            return true;
        }
        return false;
    }

    public static void readData(String filePath) {

        FileProcessor fileProcessor = new FileProcessor(filePath);
        fileProcessor.storeData();

    }
}
