package recommender;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Recommender {

    private Handler handler = null;

    // For a particular user which products to be recommended
    private Map<String, Product> recommendedProducts = new HashMap<String, Product>();

    /**
     * Constructor for the Recommender where the Handler instance is set.
     * @param handlerIn
     */
    public Recommender(Handler handlerIn) {

        handler = handlerIn;
    }

    /**
     * Parent method of the recommendation system
     */
    public void start(String[] args, boolean sentimentAnalysis) {

        if (handler != null) {
            // IP and port for consumer on 2nd and 3rd argument.
            if (sentimentAnalysis) informConsumer(args[2], Integer.parseInt(args[3]), sentimentAnalysis);
        }
    }

    public void informConsumer(String ip, int port, boolean isSentimentNeeded) {
        // Create a client to communicate to the consumer and tell that sentiment analysis is required!
        Socket clientSocket = null;
        int confirmSentimentAnalysis = 0;
        try {
            // This connects to the consumer server, tells it if we need to perform sentiment analysis.
            clientSocket = connect(ip, port);
            if (clientSocket != null) {
                OutputStreamWriter osw = new OutputStreamWriter(clientSocket.getOutputStream());
                BufferedWriter bw = new BufferedWriter(osw);
                if (isSentimentNeeded) confirmSentimentAnalysis = 1;
                bw.write(confirmSentimentAnalysis + "\n");
                bw.flush();
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String reply = br.readLine();
                if (!reply.equals("")) {
                    System.out.println(reply);
                } else {
                    System.err.println("Sentiment analysis could not be initialized.. exiting!!");
                    System.exit(-1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recommend(ConsumerRecords<Long, Product> records, String[] pythonServerDetails, boolean isSentimentNeeded) {

        if(isSentimentNeeded) {
            performSentimentAnalysis(records, pythonServerDetails);
        } else {

        }
    }

    public void performSentimentAnalysis(ConsumerRecords<Long, Product> records, String[] pythonServerDetails) {

        String ip = pythonServerDetails[0];
        int port = Integer.parseInt(pythonServerDetails[1]);
        Socket socket = null;

        try {
            socket = new Socket(ip, port);
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            //DataInputStream ds = new DataInputStream(socket.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder sb = new StringBuilder();

            for (ConsumerRecord<Long, Product> record: records) {
                // Create JSON object and send it to python code

                System.out.println(record.value());
                Product product = new Product();
                JSONObject jsonObject = product.parseString(record.value());
                sb.append(jsonObject);
                sb.append("||");
                // Converting the json object to the product class object

            }
            
            printWriter.println(sb.toString());
            printWriter.flush();

            String response = br.readLine();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Collaborative filtering to get recommended products for the input user depending on
     * what his purchased products were.
     */
    public void filterCollaboratively() {

        Iterator iterator = handler.getInput( ).entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            // This is the user who has bought something (the input user)
            String mainUser = (String) pair.getKey();
            // This is the list of products the above user has bought (the input products (products bought))
            List<String> mainProductsBought = (ArrayList<String>) pair.getValue();
            // This is the list of users who have purchased the products in the above list (mainProductsBought)
            Set<String> userList = getListOfRelevantUsers(mainProductsBought);
            // Remove self (mainUser) from userList
            userList.remove(mainUser);
            Set<String> productSet = getListOfRelevantProducts(userList, mainUser);
            System.out.println(productSet);
            for (String pr: productSet) {
                Product product = handler.getProductsMap().get(pr);
                // Check sentiment before adding it to recommendations
                if (product.getSentiment() > 0) recommendedProducts.put(mainUser, product);
            }
        }
    }

    /**
     * A helper method used to get relevant set of users for some products.
     * @param mainProductsBoughtIn
     * @return users
     */
    public Set<String> getListOfRelevantUsers(List<String> mainProductsBoughtIn) {

        Set<String> users = new HashSet<String>();

        if (mainProductsBoughtIn != null && !mainProductsBoughtIn.isEmpty()) {
            for (String product: mainProductsBoughtIn) {
                Set<String> userSet = new HashSet<String>(handler.getProductToUserMap().get(product));
                users.addAll(userSet);
                userSet.clear();
            }
        }
        return users;
    }

    /**
     * A helper method used to get relevant set of products for some products.
     * @param userSetIn
     * @return prds
     */
    public Set<String> getListOfRelevantProducts(Set<String> userSetIn, String mainUserIn) {

        Set<String> prds = new HashSet<String>();

        if(userSetIn != null && !userSetIn.isEmpty()) {
            for (String user: userSetIn) {
                Set<String> productSet = new HashSet<String>(handler.getUserToProductMap().get(user));
                // Remove already bought products
                for (String element: handler.getUserToProductMap().get(mainUserIn)) {
                    productSet.remove(element);
                }
                prds.addAll(productSet);
                productSet.clear();
            }
        }
        return prds;
    }

    /**
     * Connects to the consumer client and tells it if it has to use sent-analy or not.
     * @param ip
     * @param port
     * @return
     * @throws IOException
     */
    private Socket connect(String ip, int port) throws IOException {

        Socket socket = null;
        socket = new Socket(ip, port);
        return socket;
    }
}
