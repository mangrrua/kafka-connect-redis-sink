package connect.redis;

public class RedisEndpoint {
    private String hosts;
    private String password;
    private int timeout;
    private int dbNum;

    public RedisEndpoint(String hosts, String password, int timeout, int dbNum) {
        this.hosts = hosts;
        this.password = password;
        this.timeout = timeout;
        this.dbNum = dbNum;
    }

    public String getHosts() {
        return hosts;
    }

    public String getPassword() {
        return password;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getDbNum() {
        return dbNum;
    }
}
