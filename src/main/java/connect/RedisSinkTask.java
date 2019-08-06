package connect;

import connect.writers.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class RedisSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(RedisSinkTask.class);

    private RedisWriter redisWriter;

    @Override
    public void start(Map<String, String> props) {
        log.trace("Sink task is starting with props = {}", props);
        RedisSinkConfig config = new RedisSinkConfig(props);

        if (!config.useRecordKeyAsRedisKey && (config.keyFieldNames == null)) {
            throw new ConfigException("-redis.key.fields- configuration can not null when " +
                    "-use.record.key- configuration is false");
        }

        createRedisWriter(config);
    }

    private void createRedisWriter(RedisSinkConfig config) {
        RedisWriterType redisWriterType = RedisWriterType.get(config.redisWriter);
        switch (redisWriterType) {
            case CACHE_WRITER      : this.redisWriter = new CacheWriter(config);     break;
            case HASH_FIELD_WRITER : this.redisWriter = new HashFieldWriter(config); break;
            case HASH_VALUE_WRITER : this.redisWriter = new HashValueWriter(config); break;
        }
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        if (records.isEmpty()) {
            return;
        }

        redisWriter.write(records);
    }

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
        /*for (Map.Entry<TopicPartition, OffsetAndMetadata> entry: currentOffsets.entrySet()) {
            TopicPartition tp = entry.getKey();
            OffsetAndMetadata om = entry.getValue();
            log.debug("Flushing up to topic {}, partition {} and offset {}", tp.topic(), tp.partition(), om.offset());
        }*/
    }

    @Override
    public void stop() {
        redisWriter.shutdown();
        log.trace("Sink is stopping.");
    }

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }
}
