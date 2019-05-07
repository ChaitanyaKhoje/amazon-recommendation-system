package recommender;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class KafkaJSONDeserializer<T> implements Deserializer {

    //private Logger logger = LogManager.getLogger(this.getClass());

    private Class <T> type;

    public KafkaJSONDeserializer(Class type) {
        this.type = type;
    }

    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public Object deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T obj = null;
        try {
            obj = mapper.readValue(bytes, type);
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error(e.getMessage());
        }
        return obj;
    }

    @Override
    public void close() {

    }
}
