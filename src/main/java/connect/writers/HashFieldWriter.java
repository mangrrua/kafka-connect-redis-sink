package connect.writers;

import connect.RedisRecord;
import connect.RedisSinkConfig;
import connect.utils.DataTypeConverter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Pipeline;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HashFieldWriter extends RedisWriter {
    private static final Logger log = LoggerFactory.getLogger(HashFieldWriter.class);

    public HashFieldWriter(RedisSinkConfig config) {
        super(config);
    }

    @Override
    public void write(Collection<SinkRecord> records) {
        try (Pipeline pipelined = getRedisClient().getConnection().pipelined()) {
            for (SinkRecord record : records) {
                try {
                    RedisRecord redisRecord = getRecordConverter().get(record);
                    String redisKey = redisRecord.getKey();
                    Map<String, String> redisValues = transformValueOfMap(record.kafkaOffset(), redisRecord.getValue());

                    pipelined.hmset(redisKey, redisValues);
                }
                catch (Exception e) {
                    log.warn("Record with offset {} is skipping due to: {}", record.kafkaOffset(), e);
                }
            }

            pipelined.sync();
        }
        catch (Exception e) {
            log.error("Error occurred while getting jedis from the pool due to : {}", e.toString());
        }
    }

    private Map<String, String> transformValueOfMap(long kafkaOffset, Map<String, Object> redisValue) {
        final Map<String, String> outputValue = new HashMap<>(redisValue.size());

        for (Map.Entry<String, Object> e : redisValue.entrySet()) {
            try {
                final String key = e.getKey();
                final String valueAsString = DataTypeConverter.objectToJsonString(getObjectMapper(), e.getValue());
                outputValue.put(key, valueAsString);
            }
            catch (Exception ex) {
                log.warn("-{}- field of record with offset {} is skipping due to {}",
                        e.getKey(), kafkaOffset, ex);
            }
        }

        return outputValue;
    }
}