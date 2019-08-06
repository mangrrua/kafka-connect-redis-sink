package connect.utils;

import org.apache.kafka.connect.errors.DataException;

import java.util.Map;

public class Requirements {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> requireMap(Object value, String purpose) {
        if (!(value instanceof Map)) {
            throw new DataException(purpose);
        }

        return (Map<String, Object>) value;
    }

    public static String requireNotNullAndNonEmptyString(String value, String purpose) {
        if ((value == null) || (value.length() == 0)) {
            throw new DataException(purpose);
        }

        return value;
    }

    public static String requireString(Object value, String purpose) {
        if (!(value instanceof String)) {
            throw new DataException(purpose);
        }

        return (String) value;
    }

    public static String requireStringWithNotNullAndNonEmpty(Object value, String purpose) {
        String strVal = requireString(value, purpose);
        return requireNotNullAndNonEmptyString(strVal, purpose);
    }

    public static boolean instanceOfPrimitiveDataTypes(Object value) {
        return value instanceof String ||
                value instanceof Integer ||
                value instanceof Double ||
                value instanceof Float ||
                value instanceof Long ||
                value instanceof Byte;
    }
}
