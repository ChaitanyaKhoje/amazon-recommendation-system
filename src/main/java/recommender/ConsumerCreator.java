package recommender;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.python.google.common.io.Resources;
import util.FileProcessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

public class ConsumerCreator {

    public ConsumerCreator() { }

    public static void main(String[] args) {

        // Server to accept the message that tells what to do.
        boolean isSentimentNeeded = false;
        // Consumer declarations
        int timeouts = 0;
        //Consumer<String, Product> consumer = createConsumer();
        Consumer<Long, Product> consumer = createConsumer();
        consumer.subscribe(Collections.singletonList("amazondata"));
        Duration duration = Duration.ofSeconds(3);
        ConsumerRecords<Long, Product> records;

        Socket socket = null;
        String messageFromClient = "";
        try {
            ServerSocket serverSocket = new ServerSocket(9095);
            socket = serverSocket.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Get the line being sent by the client
            while ((messageFromClient = br.readLine()) != null) {
                if(!messageFromClient.trim().isEmpty()) break;
            }
            int msg = messageFromClient.equals("") ? 0: Integer.parseInt(messageFromClient);
            if (msg == 1) {
                // Use sentiment analysis
                System.out.println("");
                isSentimentNeeded = true;
                bw.write("Checking sentiments for product recommendations." + "\n");
            } else {
                // Do not use sentiment analysis
                System.out.println();
                bw.write("Recommending products without sentiment analysis." + "\n");
            }
            bw.flush();
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

        // Kafka Consumer starts here
        do {
            records = consumer.poll(3000);
            if (records.count() == 0) {
                System.out.println("Timeout number: " + timeouts);
                timeouts++;
            } else {
                break;
                //records.forEach(record -> System.out.println("Got record: " + record.key() + "record string: " + record.toString()));
            }
        } while (timeouts != 10000);
        if (records.count() > 0) {
            Recommender recommender = new Recommender(new Handler());
            String[] pythonServerDetails = getServerDetails();
            recommender.recommend(records, pythonServerDetails, isSentimentNeeded);
        }
    }

    /**
     * Creates and returns a new consumer object
     * @return Consumer<String, String>
     */
    private static Consumer<Long, Product> createConsumer() {

        KafkaConsumer<Long, Product> consumer = null;
        Properties properties = new Properties();
        try (InputStream props = Resources.getResource("consumer.props").openStream()) {
            properties.load(props);
            if (properties.getProperty("group.id") == null) {
                properties.setProperty("group.id", "group-" + new Random().nextInt(100000));
            }
            /*properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        consumer = new KafkaConsumer<>(properties, new StringDeserializer(), new KafkaJSONDeserializer<Product>(Product.class));

        return consumer;
    }

    /**
     * Returns python server details from a text file.
     * @return String[]
     */
    private static String[] getServerDetails() {

        String connectionsFilePath = System.getProperty("user.dir") + "/connections.txt";
        FileProcessor connProcessor = new FileProcessor(connectionsFilePath);
        String[] details = new String[2];
        while(connProcessor.hasNextLine()) {
            details = connProcessor.getNextLine().split(",");
            if (details.length != 0) {
                break;
            }
        }
        return details;
    }
}
