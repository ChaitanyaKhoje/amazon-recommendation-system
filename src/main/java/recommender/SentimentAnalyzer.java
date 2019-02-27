package recommender;

import util.Constants;

import java.io.IOException;

public class SentimentAnalyzer{

    public SentimentAnalyzer() { }

    /**
     *  Performs sentiment analysis over the reviews in the input JSON files
     *  A command is executed from this method which invokes a shell script; controller.sh
     *  The controller.sh installs required python packages and executes the python program
     *  The python program takes an argument of the directory-name/path in which the JSON files reside.
     *  The python program automatically iterates over JSON files in that directory and writes the sentiments
     *  back to those files.
     */
    public void performSentimentAnalysis() {

        ProcessBuilder builder;
        Process process = null;
        try {
            String dir = System.getProperty("user.dir") + "/";

            builder = new ProcessBuilder(dir + Constants.SHELL_SCRIPT_NAME,
                    Constants.PYTHON_PROGRAM_ARGUMENT);
            process = builder.start();
            System.out.println("Performing sentiment analysis...");
            process.waitFor();
            System.out.println("Sentiment analysis complete!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println();
            System.exit(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
                System.out.println("DEBUG: Process on analysis destroyed!");
            }
        }
    }
}
