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
        //Consumer<String, Review> consumer = createConsumer();
        Consumer<Long, Review> consumer = createConsumer();
        consumer.subscribe(Collections.singletonList("amazondata"));
        Duration duration = Duration.ofSeconds(3);
        ConsumerRecords<Long, Review> records;

        Socket socket = null;
        String messageFromClient = "";
        try {
            String[] details = FileProcessor.getServerDetails("java");
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(details[1]));
            System.out.println("Consumer initialized.");
            System.out.println("Waiting for the producer..");
            System.out.println();
            socket = serverSocket.accept();
            System.out.println("Starting up on " + socket.getInetAddress() + " port " + socket.getPort());
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Get the line being sent by the client
            while ((messageFromClient = br.readLine()) != null) {
                if(!messageFromClient.trim().isEmpty()) break;
            }
            int msg = messageFromClient.equals("") ? 0: Integer.parseInt(messageFromClient);
            if (msg == 1) {
                System.out.println();
                System.out.println("");
                // Use sentiment analysis
                System.out.println("Sentiment Analysis in progress.");
                isSentimentNeeded = true;
                bw.write("Checking sentiments for product recommendations..." + "\n");
            } else {
                // Do not use sentiment analysis
                System.out.println();
                System.out.println("Sentiment Analysis skipped!");
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
            System.out.println();
            System.out.println("Consuming records..");
            Recommender recommender = new Recommender();
            String[] pythonServerDetails = FileProcessor.getServerDetails("python");
            recommender.recommend(records, pythonServerDetails, isSentimentNeeded);
        }
    }

    /**
     * Creates and returns a new consumer object
     * @return Consumer<String, String>
     */
    private static Consumer<Long, Review> createConsumer() {

        KafkaConsumer<Long, Review> consumer = null;
        Properties properties = new Properties();
        try (InputStream props = Resources.getResource("consumer.props").openStream()) {
            properties.load(props);
            if (properties.getProperty("group.id") == null) {
                properties.setProperty("group.id", "group-" + new Random().nextInt(100000));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        consumer = new KafkaConsumer<>(properties, new StringDeserializer(), new KafkaJSONDeserializer<Review>(Review.class));

        return consumer;
    }
}
