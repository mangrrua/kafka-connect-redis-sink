package connect;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class RedisSinkConfig extends AbstractConfig {

    public static final String REDIS_WRITER = "redis.writer";
    public static final String REDIS_TYPE = "redis.type";
    public static final String USE_RECORD_KEY = "use.record.key";
    public static final String REDIS_KEY_FIELDS = "redis.key.fields";
    public static final String KEY_DELIMITER = "redis.key.delimiter";
    public static final String TABLE = "redis.table";
    public static final String REDIS_HOSTS = "redis.connection.hosts";
    public static final String TIMEOUT = "redis.connection.timeout";
    public static final String PASSWORD = "redis.connection.password";
    public static final String DB_NUMBER = "redis.dbNum";


    public final String redisWriter;
    public final String redisType;
    public final Boolean useRecordKeyAsRedisKey;
    public final String keyFieldNames;
    public final String keyDelimiter;
    public final String table;
    public final String hosts;
    public final Integer timeout;
    public final String password;
    public final Integer dbNum;


    public RedisSinkConfig(Map<String, String> props) {
        super(CONFIG_DEF, props);
        redisWriter = getString(REDIS_WRITER);
        redisType = getString(REDIS_TYPE);
        useRecordKeyAsRedisKey = getBoolean(USE_RECORD_KEY);
        keyFieldNames = getString(REDIS_KEY_FIELDS);
        keyDelimiter = getString(KEY_DELIMITER);
        table = getString(TABLE);
        hosts = getString(REDIS_HOSTS);
        timeout = getInt(TIMEOUT);
        password = getString(PASSWORD);
        dbNum = getInt(DB_NUMBER);
    }

    public static ConfigDef CONFIG_DEF = new ConfigDef()
            .define(REDIS_WRITER,
                    ConfigDef.Type.STRING,
                    "cache",
                    ConfigDef.Importance.HIGH,
                    "Type of data that will be stored in the Redis.",
                    "redis.data.config",
                    1,
                    ConfigDef.Width.MEDIUM,
                    "Redis Writer Type")
            .define(TABLE,
                    ConfigDef.Type.STRING,
                    "sinkTable",
                    ConfigDef.Importance.HIGH,
                    "If you use 'hash_value' to save data to Redis, you must specify the table name. " +
                            "This represents key in the Redis. Generated Redis records from received Kafka records " +
                            "will be saved under the these table. ",
                    "redis.data.config",
                    2,
                    ConfigDef.Width.LONG,
                    "Table Name")
            .define(USE_RECORD_KEY,
                    ConfigDef.Type.BOOLEAN,
                    true,
                    ConfigDef.Importance.HIGH,
                    "Kafka record key as Redis key value.",
                    "redis.data.config",
                    3,
                    ConfigDef.Width.SHORT,
                    "Table Name")
            .define(REDIS_KEY_FIELDS,
                    ConfigDef.Type.STRING,
                    null,
                    ConfigDef.Importance.HIGH,
                    "Field names to decide Redis key from record value. For multiple field names, " +
                            "set this value as field1:field2 format.",
                    "redis.data.config",
                    4,
                    ConfigDef.Width.LONG,
                    "Redis Primary Keys")
            .define(KEY_DELIMITER,
                    ConfigDef.Type.STRING,
                    ".",
                    ConfigDef.Importance.HIGH,
                    "Delimiter for selected multiple fields for Redis key.",
                    "redis.data.config",
                    5,
                    ConfigDef.Width.MEDIUM,
                    "Redis Primary Key Delimiter")
            .define(DB_NUMBER,
                    ConfigDef.Type.INT,
                    0,
                    ConfigDef.Importance.LOW,
                    "Database number where data will be saved.",
                    "redis.data.config",
                    6,
                    ConfigDef.Width.SHORT,
                    "DB Number")
            .define(REDIS_TYPE,
                    ConfigDef.Type.STRING,
                    "standalone",
                    ConfigDef.Importance.HIGH,
                    "Redis type that data to be saved.",
                    "redis.type",
                    1,
                    ConfigDef.Width.MEDIUM,
                    "Redis Type")
            .define(REDIS_HOSTS,
                    ConfigDef.Type.STRING,
                    "localhost:6379",
                    ConfigDef.Importance.HIGH,
                    "Host and port information to connect to the Redis. It must be host:port format." +
                            "If there is more than one host(e.g redis sentinel), it must be host:port,host1:port1 " +
                            "format.",
                    "redis.connect.connection.config",
                    1,
                    ConfigDef.Width.LONG,
                    "Redis hosts")
            .define(TIMEOUT,
                    ConfigDef.Type.INT,
                    2000,
                    ConfigDef.Importance.LOW,
                    "Maximum time to connect to the Redis.",
                    "redis.connect.connection.config",
                    2,
                    ConfigDef.Width.MEDIUM,
                    "Connection Timeout")
            .define(PASSWORD,
                    ConfigDef.Type.STRING,
                    null,
                    ConfigDef.Importance.LOW,
                    "Password of Redis.",
                    "redis.connect.connection.config",
                    3,
                    ConfigDef.Width.LONG,
                    "Redis Password");
}
