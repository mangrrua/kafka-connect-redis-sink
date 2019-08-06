package connect.writers;

import connect.RedisRecord;
import connect.RedisSinkConfig;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static connect.utils.DataTypeConverter.objectToJsonString;

public class HashValueWriter extends RedisWriter {
    private static final Logger log = LoggerFactory.getLogger(HashValueWriter.class);

    private String table;

    public HashValueWriter(RedisSinkConfig config) {
        super(config);
        this.table = config.table;
    }

    @Override
    public void write(Collection<SinkRecord> records) {
        try (Jedis connection = getRedisClient().getConnection()) {
            Map<String, String> outputValues = new HashMap<>();

            for (SinkRecord record : records) {
                try {
                    RedisRecord redisRecord = getRecordConverter().get(record);
                    String redisKey = redisRecord.getKey();
                    Map<String, Object> redisValue = redisRecord.getValue();
                    String valueAsString = objectToJsonString(getObjectMapper(), redisValue);

                    outputValues.put(redisKey, valueAsString);
                }
                catch (Exception e) {
                    log.warn("Record with offset {} is skipping due to: {}", record.kafkaOffset(), e);
                }
            }

            connection.hmset(table, outputValues);
        }
        catch (Exception e) {
            log.error("Error occurred while getting jedis from the pool due to : {}", e.toString());
        }
    }
}
