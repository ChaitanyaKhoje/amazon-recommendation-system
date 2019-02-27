package recommender;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.util.Properties;

public class ConsumerCreator {

    public ConsumerCreator() { }

    /*public static void main(String[] args) {

        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);*/
        //List topics = new ArrayList();
        //topics.add("devglan-test");
        //kafkaConsumer.subscribe(topics);
//        try {
//            while (true) {
//                Duration duration = Duration.ofSeconds(5);
//                ConsumerRecords<Long, String> records = kafkaConsumer.poll(duration);
//                for (ConsumerRecord record : records) {
//                    System.out.println(String.format("Topic - %s, Partition - %d, Value: %s", record.topic(), record.partition(), record.value()));
//                }
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        } finally {
//            kafkaConsumer.close();
//        }
    //}

    public static Consumer createConsumer() {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test-group");

        return new KafkaConsumer(properties);
    }
}
