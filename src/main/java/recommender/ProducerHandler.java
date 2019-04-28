package recommender;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import util.FileProcessor;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class ProducerHandler implements Runnable {

    private final Producer<String, String> producer;
    private String dataPath;
    private String topic;
    private int sleepTime = 0;

    public ProducerHandler(String dataDirectoryPath, Producer<String, String> producerIn, String topicIn, int sleepTimeIn) {

        dataPath = dataDirectoryPath;
        producer = producerIn;
        topic = topicIn;
        sleepTime = sleepTimeIn;
    }

    @Override
    public void run() {

        // Iterate over all JSON files and produce.
        // TODO: Think about how to read the data, file by file? or arbitrarily thru different files. Or all at once.

        /** Procedure:
         *  Take in the dir path given as program argument.
         *  Iterate over all JSON files and read product data
         *  Take out reviews for all products in a JSON file and build it as a producer record one by one
         *  Send these producer records as they are built, to the kafka broker.
         */

        double count = 0;  // Counter to check if we are to pause for some while and start sending again.
        File dir = new File(getDataPath());
        FileProcessor fp = null;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".json")) {
                        // Clear previous fileprocessor object
                        if (fp != null) fp = null;
                        fp = new FileProcessor(file.getPath());
                        // TODO: Send messages thru kafka broker instead of populating in memory
                        while (fp.hasNextLine()) {
                            count++; // Counter to check how many messages were sent.
                            if ((count % 100) == 0) {
                                try {
                                    Thread.sleep(getSleepTime());
                                } catch (InterruptedException e) {
                                    System.out.println("DEBUG: PROBLEM IN THREAD SLEEP");
                                    e.printStackTrace();
                                }
                            }
                            String line = fp.getNextLine();

                            RecordMetadata m = null;
                            try {
                                m = producer.send(new ProducerRecord<String, String>(getTopic(), line)).get();
                                System.out.println(m.toString());
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Message produced, offset: " + m.offset());
                            System.out.println("Message produced, partition : " + m.partition());
                            System.out.println("Message produced, topic: " + m.topic());
                            //producer.send(new ProducerRecord<String, String>(getTopic(), line));
                            //System.out.println(line);
                        }
                    }
                }
            }
        }
        producer.close();
    }

    public Producer getProducer() {
        return producer;
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getTopic() {
        return topic;
    }

    public int getSleepTime() {
        return sleepTime;
    }
}
