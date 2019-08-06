package connect.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class RedisStandalone implements RedisClient {
    private static final Logger log = LoggerFactory.getLogger(RedisStandalone.class);

    private static RedisStandalone redisStandalone;
    private RedisEndpoint redisEndpoint;
    private JedisPool jedisPool;

    private RedisStandalone(RedisEndpoint re) {
        this.redisEndpoint = re;
        createPool();
    }

    public static RedisStandalone getInstance(RedisEndpoint re) {
        if (redisStandalone == null) {
            redisStandalone = new RedisStandalone(re);
        }

        return redisStandalone;
    }

    @Override
    public Jedis getConnection() {
        return jedisPool.getResource();
    }

    @Override
    public void destroy() {
        if (jedisPool != null) {
            jedisPool.destroy();
            log.info("Jedis pool successfully destroyed.");
        }
    }

    private void createPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(32);
        poolConfig.setMaxIdle(16);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(false);
        poolConfig.setMinEvictableIdleTimeMillis(60000);
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        poolConfig.setNumTestsPerEvictionRun(-1);

        String[] hostAndPort = redisEndpoint.getHosts().split(":");
        String host = hostAndPort[0];
        int port = Integer.valueOf(hostAndPort[1]);

        if (redisEndpoint.getPassword() == null) {
            jedisPool = new JedisPool(poolConfig, host, port, redisEndpoint.getTimeout());
        }
        else {
            jedisPool = new JedisPool(poolConfig, host, port, redisEndpoint.getTimeout(),
                    redisEndpoint.getPassword(), redisEndpoint.getDbNum());
        }

        log.info("Jedis pool successfully created.");
    }
}
