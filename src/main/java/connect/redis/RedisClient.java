package connect.redis;

import redis.clients.jedis.Jedis;

public interface RedisClient {

    Jedis getConnection();
    void destroy();
}
