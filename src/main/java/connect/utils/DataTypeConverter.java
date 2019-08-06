package connect.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataTypeConverter {

    public static String objectToJsonString(ObjectMapper mapper, Object value) throws JsonProcessingException {
        /*if (value == null) {
            throw new NullPointerException("Null object can not converted to string.");
        }*/

        return mapper.writeValueAsString(value);
    }
}
