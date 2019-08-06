package connect.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RedisSentinel implements RedisClient {
    private static final Logger log = LoggerFactory.getLogger(RedisSentinel.class);

    private static RedisSentinel redisSentinel;
    private RedisEndpoint redisEndpoint;
    private JedisSentinelPool jedisSentinelPool;

    private RedisSentinel(RedisEndpoint re) {
        this.redisEndpoint = re;
        createRedisSentinelPool();
    }

    public static RedisSentinel getInstance(RedisEndpoint re) {
        if (redisSentinel == null) {
            redisSentinel = new RedisSentinel(re);
        }

        return redisSentinel;
    }

    @Override
    public Jedis getConnection() {
        return jedisSentinelPool.getResource();
    }

    @Override
    public void destroy() {
        if (jedisSentinelPool != null) {
            jedisSentinelPool.destroy();
            log.info("Jedis sentinel pool successfully destroyed.");
        }
    }

    private void createRedisSentinelPool() {
        Set<String> sentinels = new HashSet<String>(Arrays.asList(redisEndpoint.getHosts().split(",")));

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(32);
        poolConfig.setMaxIdle(16);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(false);
        poolConfig.setMinEvictableIdleTimeMillis(60000);
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        poolConfig.setNumTestsPerEvictionRun(-1);

        if (redisEndpoint.getPassword() == null) {
            jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels, poolConfig, redisEndpoint.getTimeout());
        }
        else {
            jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels, poolConfig, redisEndpoint.getTimeout(),
                    redisEndpoint.getPassword(), redisEndpoint.getDbNum());
        }

        log.info("Jedis sentinel pool successfully created.");
    }
}
