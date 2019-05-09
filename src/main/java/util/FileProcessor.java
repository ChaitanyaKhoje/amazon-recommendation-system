package util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileProcessor {

    private Scanner scanner = null;
    private String fileLine = " ";

    /**
     * Constructor for FileProcessor; takes in the file path and initializes the scanner with it.
     *
     * @param filePathIn input.json/delete.txt path is passed for Scanner.
     */
    public FileProcessor(String filePathIn) {

        File file = null;
        if (!filePathIn.isEmpty() || !filePathIn.equals("")) {
            file = new File(filePathIn);
        }
        try {
            if (file != null) this.scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            System.exit(-1);
        } catch (NullPointerException npe) {
            System.err.println("Please check the program arguments passed and rerun.");
        } finally {
            System.out.println();
        }
    }

    /**
     * Returns python server details from a text file.
     * @return String[]
     */
    public static String[] getServerDetails(String server) {

        // Connections file has two servers, one for java (consumer) and another for python (sent analysis)
        String connectionsFilePath = System.getProperty("user.dir") + "/connections.json";
        FileProcessor connProcessor = new FileProcessor(connectionsFilePath);
        String[] details = new String[2];
        while(connProcessor.hasNextLine()) {
            String line = connProcessor.getNextLine();
            JSONObject obj = new JSONObject(line);
            JSONArray arr = obj.getJSONArray("servers");

            for(int i = 0; i < arr.length(); i++) {
                JSONObject j = arr.getJSONObject(i);
                if (j.has(server)) {
                    JSONArray lang = j.getJSONArray(server);
                    details[0] = lang.getJSONObject(0).getString("ip");
                    details[1] = lang.getJSONObject(1).getString("port");
                    break;
                }
            }
        }
        return details;
    }

    /**
     * This method is called from Results class. It writes to the three output files.
     *
     * @param resultIn   The result to be written to the file.
     * @param filePathIn The file to write the result in.
     */
    public static void write(String resultIn, String filePathIn) {

        File file;
        BufferedWriter bufferedWriter = null;
        try {
            file = new File(filePathIn);
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(resultIn);
        } catch (IOException ioe) {
            System.out.println("One or more output files were not found!");
        } finally {
            try {
                if (bufferedWriter != null) bufferedWriter.close();
            } catch (IOException e) {
                System.out.println();
            }
        }
    }

    /**
     * Gets a single line from a file.
     *
     * @return returns a single file line.
     */
    public String getNextLine() {

        String fileLine;
        try {
            fileLine = scanner.nextLine();
        } finally {
            if (scanner == null) {
                System.err.println("The file could not be found or the file is empty!");
            }
        }
        return fileLine;
    }

    /**
     * Used while iterating over input.json and delete.txt
     *
     * @return returns true if there is another line in the input.
     */
    public boolean hasNextLine() {

        return this.scanner.hasNextLine();
    }

    @Override
    public String toString() {
        return "FileProcessor{}";
    }
}
