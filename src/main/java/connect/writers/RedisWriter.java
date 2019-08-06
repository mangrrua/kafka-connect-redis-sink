package connect.writers;

import com.fasterxml.jackson.databind.ObjectMapper;
import connect.RecordConverter;
import connect.RedisSinkConfig;
import connect.redis.*;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Collection;

public abstract class RedisWriter {

    private ObjectMapper objectMapper;
    private RecordConverter recordConverter;
    private RedisClient redisClient;

    public RedisWriter(RedisSinkConfig config) {
        this.objectMapper = new ObjectMapper();

        createRedisConnection(config);
        createRecordConverter(config);
    }

    private void createRedisConnection(RedisSinkConfig config) {
        String hosts = config.hosts;
        String password = config.password;
        Integer dbNum = config.dbNum;
        Integer timeout = config.timeout;

        RedisEndpoint re = new RedisEndpoint(hosts, password, timeout, dbNum);

        RedisType redisType = RedisType.get(config.redisType);
        switch (redisType) {
            case STANDALONE: redisClient = RedisStandalone.getInstance(re); break;
            case SENTINEL  : redisClient = RedisSentinel.getInstance(re);   break;
        }
    }

    private void createRecordConverter(RedisSinkConfig config) {
        String specifiedKeyFieldNames = config.keyFieldNames;
        String keyDelimiter = config.keyDelimiter;
        boolean useRecordKeyAsRedisKey = config.useRecordKeyAsRedisKey;
        this.recordConverter = new RecordConverter(specifiedKeyFieldNames, keyDelimiter, useRecordKeyAsRedisKey);
    }

    protected RedisClient getRedisClient() {
        return redisClient;
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected RecordConverter getRecordConverter() {
        return recordConverter;
    }

    public void shutdown () {
        redisClient.destroy();
    }

    public abstract void write(Collection<SinkRecord> records);
}
