package util;

public class Results implements FileDisplayInterface, StdoutDisplayInterface {

    private StringBuilder sb = new StringBuilder();
    private String result = "";

    /**
     * Writes the output to the three output files.
     *
     * @param resultIn   The result to be written to the file.
     * @param filePathIn The file to write the result in.
     */
    @Override
    public void writeOutput(String resultIn, String filePathIn) {

        FileProcessor.write(resultIn, filePathIn);
    }

    @Override
    public String display(boolean graduated) {

        result = buildOutput(graduated);
        System.out.println("Output: " + result);
        return result;
    }


    /**
     * String builder is used to append output.
     * Used to display in the console.
     */
    private String buildOutput(boolean graduated) {

        String output = "";
        return output;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String resultIn) {
        result = result;
    }
}
