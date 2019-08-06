package connect;

import java.util.Map;

public class RedisRecord {

    private String key;
    private Map<String, Object> value;

    public RedisRecord(String key, Map<String, Object> value) {
        this.value = value;
        this.key = key;
    }

    public Map<String, Object> getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
