package recommender;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.python.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProducerCreator {

    public ProducerCreator() { }

    public static Producer<String, String> createProducer() {

        KafkaProducer<String, String> producer = null;
        try (InputStream props = Resources.getResource("producer.props").openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            producer = new KafkaProducer<>(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return producer;
    }
}
