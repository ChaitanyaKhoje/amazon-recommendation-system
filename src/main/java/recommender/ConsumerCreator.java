package recommender;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.python.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

public class ConsumerCreator {

    public ConsumerCreator() { }

    public static void main(String[] args) {

        int timeouts = 0;
        Consumer<String, String> consumer = createConsumer();
        consumer.subscribe(Collections.singletonList("amazondata"));

        Duration duration = Duration.ofSeconds(3);
        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(duration);
            if (records.count() == 0) {
                timeouts++;
            } else {
                records.forEach(record -> System.out.println("Got record: " + record.key() + "record string: " + record.toString()));
            }
            if (timeouts == 2000) break;
        }
    }

    public static Consumer<String, String> createConsumer() {

        KafkaConsumer<String, String> consumer = null;
        try (InputStream props = Resources.getResource("consumer.props").openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            if (properties.getProperty("group.id") == null) {
                properties.setProperty("group.id", "group-" + new Random().nextInt(100000));
            }
            consumer = new KafkaConsumer<>(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return consumer;
    }


}
