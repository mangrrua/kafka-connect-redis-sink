package connect.writers;

import connect.RedisRecord;
import connect.RedisSinkConfig;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Pipeline;

import java.util.Collection;

import static connect.utils.DataTypeConverter.objectToJsonString;

public class CacheWriter extends RedisWriter {
    private static final Logger log = LoggerFactory.getLogger(CacheWriter.class);

    public CacheWriter(RedisSinkConfig config) {
        super(config);
    }

    @Override
    public void write(Collection<SinkRecord> records) {
        try (Pipeline pipelined = getRedisClient().getConnection().pipelined()) {
            for (SinkRecord record : records) {
                try {
                    RedisRecord redisRecord = getRecordConverter().get(record);
                    String redisKey = redisRecord.getKey();
                    String redisValue = objectToJsonString(getObjectMapper(), redisRecord.getValue());

                    pipelined.set(redisKey, redisValue);
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


}