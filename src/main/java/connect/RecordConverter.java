package connect;

import connect.utils.Requirements;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.*;


public class RecordConverter {

    private String keyPrefix;
    private List<String> keyFieldNames;
    private String keyDelimiter;
    private boolean useRecordKeyAsRedisKey;

    public RecordConverter(String keyPrefix, String specifiedKeyFieldNames, String keyDelimiter, boolean useRecordKeyAsRedisKey) {
        if (specifiedKeyFieldNames != null) {
            this.keyFieldNames = Arrays.asList(specifiedKeyFieldNames.split(":"));
            this.keyDelimiter = keyDelimiter;
        }
        this.keyPrefix = keyPrefix;
        this.useRecordKeyAsRedisKey = useRecordKeyAsRedisKey;
    }

    public RedisRecord get(SinkRecord record) {
        Object recordKey = record.key();
        Object recordValue = record.value();

        Map<String, Object> recordValueAsMap = Requirements.requireMap(recordValue, "Record value " +
                "must be Map type, and can not be null.");
        return getOutputData(recordKey, recordValueAsMap);
    }

    private RedisRecord getOutputData(Object recordKey, Map<String, Object> recordValue) {
        if (useRecordKeyAsRedisKey) {
            String redisKey = Requirements.requireStringWithNotNullAndNonEmpty(recordKey,
                    "Only String types is supported to assign record key as Redis key. Also, " +
                            "record key can not null or empty when used as Redis key.");
            return new RedisRecord(composeRedisKey(redisKey), recordValue);
        }
        else {
            String redisKey = extractRedisKeyFromRecordValue(recordValue);
            Map<String, Object> redisValue = extractRedisValueFromRecordValue(recordValue);
            return new RedisRecord(composeRedisKey(redisKey), redisValue);
        }
    }

    private String composeRedisKey(String key) {
        if(keyPrefix == null || keyPrefix.isEmpty()) {
            return key;
        }

        return keyPrefix + key;
    }

    private Map<String, Object> extractRedisValueFromRecordValue(Map<String, Object> recordValue) {
        final Map<String, Object> outputValue = new HashMap<>(recordValue.size());

        for (Map.Entry<String, Object> e : recordValue.entrySet()) {
            final String fieldName = e.getKey();
            final Object fieldValue = e.getValue();

            if (!isKey(fieldName))
                outputValue.put(fieldName, fieldValue);
        }

        return outputValue;
    }

    private String extractRedisKeyFromRecordValue(Map<String, Object> recordValue) {
        StringJoiner keyJoiner = new StringJoiner(keyDelimiter);
        for (String keyName : keyFieldNames) {
            Object pkVal = recordValue.get(keyName);

            if (Requirements.instanceOfPrimitiveDataTypes(pkVal))
                keyJoiner.add(pkVal.toString());
        }

        return Requirements.requireNotNullAndNonEmptyString(
                keyJoiner.toString(),
                "Selected Redis key fields not found or empty.");
    }

    private boolean isKey(String fieldName) {
        return keyFieldNames.contains(fieldName);
    }

}