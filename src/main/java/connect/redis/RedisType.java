package connect.redis;

public enum RedisType {

    STANDALONE("standalone"),
    SENTINEL("sentinel");

    private final String name;

    RedisType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static RedisType get(String name) {
        for (RedisType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }

        String errorMessage = "Unsupported Redis Type: <" + name + ">. Use these possible values: \n";
        StringBuilder builder = new StringBuilder(errorMessage);
        for(RedisType type : values()) {
            builder.append(type.name);
            builder.append("\n");
        }

        throw new IllegalArgumentException(builder.toString());
    }
}
