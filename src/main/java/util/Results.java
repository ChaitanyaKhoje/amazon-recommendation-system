package util;

import java.util.Map;

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
    public String display(Map<String, String> input) {

        result = buildOutput(input).trim();
        System.out.println(System.lineSeparator());
        if (result.equals("")) {
            result = "Oops! Looks like we don't have enough data to recommend!\nFeed me some product-review data please!" ;
        } else {
            System.out.println(" --- Recommendations --- ");
        }
        return result;
    }


    /**
     * String builder is used to append output.
     * Used to display in the console.
     * @param input
     */
    private String buildOutput(Map<String, String> input) {

        String output;
        for (Map.Entry e: input.entrySet()) {
            sb.append("For customer: ").append(e.getKey())
                    .append(" item ").append(e.getValue()).append(" is recommended!");
        }
        output = sb.toString();
        return output;
    }
}
