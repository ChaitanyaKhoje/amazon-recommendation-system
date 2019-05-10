package recommender;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.json.JSONObject;
import util.FileProcessor;
import util.Results;

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
    private Map<String, String> recommendedProducts = new HashMap<String, String>();

    public Recommender() { }

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
    public void start(boolean sentimentAnalysis) {

        informConsumer(sentimentAnalysis);
    }

    public void informConsumer(boolean isSentimentNeeded) {
        // Create a client to communicate to the consumer and tell that sentiment analysis is required!
        Socket clientSocket = null;
        int confirmSentimentAnalysis = 0;
        try {
            // This connects to the consumer server, tells it if we need to perform sentiment analysis.
            String[] consumerServerDetails = FileProcessor.getServerDetails("java");
            clientSocket = connect(consumerServerDetails[0], Integer.parseInt(consumerServerDetails[1]));
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

    public void recommend(ConsumerRecords<Long, Review> records, String[] pythonServerDetails, boolean isSentimentNeeded) {

        // Store new handler object into this recommender instance.
        handler = new Handler();
        // Store in-memory and update sentiment later on if required
        handler.populateInMemory(records);
        // Response from python program
        String response = "";
        handler.parseInputData();
        if(isSentimentNeeded) {
            response = performSentimentAnalysis(records, pythonServerDetails);
            handler.mapDetails(response);
            filterCollaboratively(response);
        } else {

        }
        Results results = new Results();
        System.out.println(results.display(recommendedProducts));
    }

    public String performSentimentAnalysis(ConsumerRecords<Long, Review> records, String[] pythonServerDetails) {

        String ip = pythonServerDetails[0];
        int port = Integer.parseInt(pythonServerDetails[1]);
        Socket socket = null;
        String response = "";
        try {
            socket = new Socket(ip, port);
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            //DataInputStream ds = new DataInputStream(socket.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();

            for (ConsumerRecord<Long, Review> record: records) {
                // Create JSON object and send it to python code
                Review review = new Review();
                JSONObject jsonObject = review.getJSONObjectForProduct(record.value());
                sb.append(jsonObject);
                sb.append("||");
            }
            System.out.println("Contacting sentiment analysis module...");
            printWriter.write(sb.toString());
            //printWriter.println(sb.toString());
            printWriter.flush();
            response = br.readLine();
            System.out.println("Sentiment analysis completed, sending results to the product recommender.");
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
        return response;
    }

    /**
     * Collaborative filtering to get recommended products for the input user depending on
     * what his purchased products were.
     */
    public void filterCollaboratively(String response) {

        Iterator iterator = handler.getInput().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            // This is the user who has bought something (the input user)
            String mainUser = (String) pair.getKey();
            // Get the id instead of user's name
            if (!handler.getUsers().isEmpty()) mainUser = handler.getUsers().get(mainUser);
            // This is the list of products the above user has bought (the input products (products bought))
            List<String> mainProductsBought = (ArrayList<String>) pair.getValue();
            // This is the list of users who have purchased the products in the above list (mainProductsBought)
            Set<String> userList = getListOfRelevantUsers(mainProductsBought);
            // Remove self (mainUser) from userList
            userList.remove(mainUser);
            Set<String> productSet = getListOfRelevantProducts(userList, mainUser);

            // Remove bought products from recos
            for (String prod: mainProductsBought) {
                productSet.remove(prod);
            }

            if (productSet.isEmpty()) {
                //  If collaborative filtering yields nothing, check for existing products,
                // i.e check what our new customer has bought in the past and match with other users
                productSet = getPastPurchase(mainUser);
            }
            int sentiment = 0; // zero is neutral
            Map<Integer, Integer> sentiments = new HashMap<Integer, Integer>();
            for (String pr: productSet) {
                Set<String> reviews;
                if (!handler.getReviewSet().isEmpty()) {
                    for (Review review: handler.getReviewSet()) {
                        if (review.getAsin().equals(pr)) {
                            // Check sentiments user by user
                            sentiments.put(review.getSentiment(),
                                    sentiments.getOrDefault(review.getSentiment(), 0) + 1);
                        }
                    }
                }
                // get maximum of 1's or 0's and select the sentiment accordingly
                Map.Entry<Integer, Integer> maxEntry = null;
                for (Map.Entry<Integer, Integer> entry : sentiments.entrySet()) {
                    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                        maxEntry = entry;
                    }
                }
                sentiment = maxEntry.getKey();
                // Check sentiment before adding it to recommendations
                if (sentiment >= 0) recommendedProducts.put(mainUser, pr);
            }
        }
    }

    /**
     * Get past purchases for a customer
     * @param mainUser
     * @return
     */
    public Set<String> getPastPurchase(String mainUser) {

        Set<String> productSet = new HashSet<>();

        if(!handler.getUserToProductMap().isEmpty()) {
            // We have our user to products mapping, now check our current customer's past products
            Set<String> pastProducts = handler.getUserToProductMap().get(mainUser);
            // List of users who bought above products
            Set<String> userList = getListOfRelevantUsers(new ArrayList<>(pastProducts));
            userList.remove(mainUser);  // Remove current customer from this list (happens when there are commons products between 2 or more users)
            productSet = getListOfRelevantProducts(userList, mainUser);
        } else {
            System.out.println("Looks like we don't have any purchases yet!");
        }

        return productSet;
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
     * A helper method used to get relevant set of products for some users.
     * @param userSetIn
     * @return prds
     */
    public Set<String> getListOfRelevantProducts(Set<String> userSetIn, String mainUserIn) {

        Set<String> prds = new HashSet<String>();

        if(userSetIn != null && !userSetIn.isEmpty()) {
            for (String user: userSetIn) {
                Set<String> productSet = new HashSet<String>(handler.getUserToProductMap().get(user));
                // Remove already bought products
                Set<String> elements
                        = handler.getUserToProductMap().get(mainUserIn) == null ?
                        new HashSet<>() : handler.getUserToProductMap().get(mainUserIn);
                if (!elements.isEmpty()) {
                    for (String element : elements) {
                        productSet.remove(element);
                    }
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
