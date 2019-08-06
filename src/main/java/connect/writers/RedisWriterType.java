package connect.writers;


public enum RedisWriterType {

    HASH_FIELD_WRITER("hash_field"),
    HASH_VALUE_WRITER("hash_value"),
    CACHE_WRITER("cache");

    private String name;

    RedisWriterType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static RedisWriterType get(String name) {
        for (RedisWriterType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }

        String errorMessage = "Unsupported Redis writer: <" + name + ">. Use these possible values: \n";
        StringBuilder builder = new StringBuilder(errorMessage);
        for(RedisWriterType type : values()) {
            builder.append(type.name);
            builder.append("\n");
        }

        throw new IllegalArgumentException(builder.toString());
    }
}
