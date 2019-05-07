package recommender;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.python.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProducerCreator {

    public ProducerCreator() { }

    public static Producer<String, Product> createProducer() {

        Producer<String, Product> producer = null;
        try (InputStream props = Resources.getResource("producer.props").openStream()) {
            Properties properties = new Properties();
            properties.load(props);

            /*properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);*/
            producer = new KafkaProducer<>(properties, new StringSerializer(), new KafkaJSONSerializer());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return producer;
    }
}
