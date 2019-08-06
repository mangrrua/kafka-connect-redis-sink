import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import connect.RedisSinkTask;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RedisSinkTaskTest {

    ObjectMapper mapper = new ObjectMapper();
    RedisSinkTask sinkTask = Mockito.spy(RedisSinkTask.class);
    Map<String, String> generalConfigs = getGeneralConfigs();

    private Map<String, String> getGeneralConfigs() {
        Map<String, String> config = new HashMap<>();

        config.put("redis.type", "standalone");
        config.put("redis.key.fields", "id");
        config.put("use.record.key", "true");
        //config.put("redis.key.delimiter", ".");
        config.put("redis.connection.hosts", "host:6379");
        config.put("redis.connection.password", "test_password");
        config.put("redis.connection.timeout", "2000");

        return config;
    }

    private Map<String, String> getCacheWriterConfigs(Map<String, String> config) {
        config.put("redis.writer", "cache");

        return config;
    }

    private Map<String, String> getHashFieldWriterConfigs(Map<String, String> config) {
        config.put("redis.writer", "hash_field");

        return config;
    }

    private Map<String, String> getHashValueWriterConfigs(Map<String, String> config) {
        config.put("redis.writer", "hash_value");
        config.put("redis.table", "kafka-sink-test-table");

        return config;
    }


    @Test
    public void cacheWriterTest() throws Exception {
        Map<String, String> cacheWriterConfigs = getCacheWriterConfigs(generalConfigs);

        sinkTask.start(cacheWriterConfigs);
        sinkTask.put(getRecords());
        sinkTask.stop();
    }

    @Test
    public void hashFieldWriterTest() throws Exception {
        Map<String, String> hashFieldWriterConfigs = getHashFieldWriterConfigs(generalConfigs);

        sinkTask.start(hashFieldWriterConfigs);
        sinkTask.put(getRecords());
        sinkTask.stop();
    }

    @Test
    public void hashValueWriterTest() throws Exception {
        Map<String, String> hashValueWriterConfigs = getHashValueWriterConfigs(generalConfigs);

        sinkTask.start(hashValueWriterConfigs);
        sinkTask.put(getRecords());
        sinkTask.stop();
    }


    private String getExampleRecordValue() throws Exception {
        ObjectNode subJsonDataObj = mapper.createObjectNode();
        subJsonDataObj.put("name", "Shelly");
        subJsonDataObj.put("surname", "Stark");

        ObjectNode jsonDataObj = mapper.createObjectNode();
        jsonDataObj.put("id", "p25");
        //jsonDataObj.putPOJO("subJsonObj", subJsonDataObj);
        jsonDataObj.put("addr", "address");

        return mapper.writeValueAsString(jsonDataObj);
    }

    private String getExampleRecordValueWithoutSpecifiedKey() throws Exception {
        ObjectNode jsonDataObj = mapper.createObjectNode();
        jsonDataObj.put("id", "id5");
        jsonDataObj.put("addr2", "undefined");
        jsonDataObj.put("addr3", "undefined");

        return mapper.writeValueAsString(jsonDataObj);
    }

    public Collection<SinkRecord> getRecords() throws Exception {
        SinkRecord record = new SinkRecord(
                "topic",
                1,
                Schema.STRING_SCHEMA,
                "test",
                Schema.STRING_SCHEMA,
                getExampleRecordValue(),
                1001L);

        SinkRecord record1 = new SinkRecord(
                "topic",
                1,
                Schema.STRING_SCHEMA,
                null,
                Schema.STRING_SCHEMA,
                getExampleRecordValue(),
                1002L);

        SinkRecord record2 = new SinkRecord(
                "topic",
                1,
                Schema.STRING_SCHEMA,
                null,
                Schema.STRING_SCHEMA,
                getExampleRecordValueWithoutSpecifiedKey(),
                1002L);


        Collection<SinkRecord> records = new ArrayList<>();
        records.add(record);
        records.add(record1);
        records.add(record2);

        return records;
    }
}
