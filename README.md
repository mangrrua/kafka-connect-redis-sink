# Kafka Connect Sink Connector for Redis
`kafka-connect-redis-sink` is used to save data from Kafka to Redis with different data types options.


## Contents
- [Building the Connector](#building-the-connector)
- [Project Dependencies](#project-dependencies)
- [Features](#features)
- [Supported Redis Data Types](#supported-redis-data-types)
- [Sink Properties](#sink-properties)


## Building the Connector
- Maven 3.1 or later
- Java 8 or later
- Git (optional)

Clone repository using following command (or directly download project): 
```shell
git clone https://github.com/mangrrua/kafka-connect-redis-sink.git
```

Go to the project directory:
```shell
cd kafka-connect-redis-sink
```

Build project: 
```shell
mvn clean package
```

Jar with dependencies will be created in the `/target` directory as `project-name-1.0-jar-with-dependencies.jar` name.


## Project Dependencies
- [Jedis](https://github.com/xetorthio/jedis) is used to connect to the Redis.
- [FasterXml Jackson](https://github.com/FasterXML/jackson) is used to convert output value of record to `JSON` string. 
- [JUnit](https://junit.org/junit5/) and [Mockito](https://site.mockito.org/) are used for testing purpose. 


## Features
This redis sink connector supports `cache(string)` and `hashes` data types([redis data formats](https://redis.io/topics/data-types-intro)) with different options. You can write `JSON` data that stored in the Kafka topics to Redis with supported data formats and different Redis key options. 


It works with only `Schemaless JSON` data now(schema support will be added in later versions). Thus, you must specify `value.converter` and `value.converter.schemas.enable` properties in the connect `standalone/distributed.properties` like below;

```shell
value.converter=org.apache.kafka.connect.json.JsonConverter
value.converter.schemas.enable=false
```

Connector, receives JSON data as `Map<String, Object>` format, and generates output according to your configurations. If record value is not a Map(not a JSON), connector ignores record. 

You can specify Redis key with two way;
1. **Use Kafka Record key as Redis Key:** With this option, record key will be used for Redis key, and record value will be used as Redis value. If you will use this option, your `key.converter` and `key.converter.schemas.enable` properties in the connect `standalone/distributed.properties` must be like below;
    ```shell
    key.converter=org.apache.kafka.connect.storage.StringConverter
    key.converter.schemas.enable=false
    ```
    
    In this situation, if record key value is String, not null and non empty, key value is assigned as Redis key. Otherwise, connector ignores this record, and does not sends it to Redis
     
2. **Use Any Fields of Record Value:** With this option, you can select any fields from your JSON input(record value), and values of these selected fields will be used as Redis key. If more than one fields are selected, `redis.key.delimiter` is used to split Redis key.  The remaining fields will be used as Redis value according to selected Redis data type(cache or hashes).
     
    Record will be saved to Redis if at least one selected field is primitive data type and not null and non empty. Otherwise, connector ignores this record, and does not sends it to Redis. 


`use.record.key` option is configurable to which option is used to decide Redis key.    
    
Connector also supports `Redis Standalone` and `Redis Sentinel` connection. For this `Jedis` was used due to lightweight api. 

## Supported Redis Data Types
This connector supports two data types to write data to Redis: string(cache) and hashes. For `hashes` data structures, connector also supports two write option;
- String(cache) Data Type - Cache Writer
- Hashes Data Type
   * Hash Field Writer
   * Hash Value Writer
   
You can choose any writer you want using `redis.writer` property.

**1. String Data Type(CacheWriter)**

In this format, Redis `"set redisKey redisValue"` is used. Redis key and value will be genereted according to your configurations. Output value that will be saved as Redis value converted to `JSON` string after generated. Finally, output records will be saved to Redis. 

Example input data in Kafka topic;
```shell
record key   : "96533" 
record value : {"id": "125", "name": "shelly", "surname": "stark", "age": "25", "address": "address", "job": "Computer Engineer"}
```
If you set `use.record.key` to `true`, Redis key will be `96533`, and Redis value will be a JSON string;
```shell
{"id": "125", "name": "shelly", "surname": "stark", "age": "25", "address": "address", "job": "Computer Engineer"}
```
If you set `use.record.key` to `false`, and selected field name for redis key is `id`, Redis key will be `125`, and Redis value will be like;
```shell
{"surname": "stark", "age": "25", "address": "address", "job": "Computer Engineer"}
```
You can select multiple fields for Redis key such as `name` and `surname`. Also, you can set `redis.key.delimiter` you want(for example `-`) . In this case, Redis key will be `shelly-stark`. And Redis value will be like;
```shell
{"id": "125", "age": "25", "address": "address", "job": "Computer Engineer"}
```

**2. Hashes Data Type**

Connector uses Redis `"hmset redisKey subKey subValue subKey1 subValue1"` command. You can use two writer for Hashes data structures. 

- `Hash_field Writer`

    This writer receives your record data, generates Redis key, and converts each field of record value to `subKey - subValue` format for Redis Hashes structure, then it saves these fields under the generated Redis key. 

    Example input data in Kafka topic;
    ```shell
    record key   : "96533" 
    record value : {"id": "125", "name": "shelly", "surname": "stark", "age": "25", "address": "address", "job": "Computer Engineer"}
    ```
    
    If you set `use.record.key` to `true`, Redis key will be `96533`, and Redis value will be like(value is basically Map format);
    ```shell
    "id": "125", 
    "name": "shelly", 
    "surname": "stark", 
    "age": "25", 
    "address": "address", 
    "job": "Computer Engineer"
    ```
    
    If you set `use.record.key` to `false`, and selected field names are `name` and `surname`, Redis key will be ```shelly.stark```, and Redis value will be like;
    ```shell
    "id": "125", 
    "age": "25", 
    "address": "address", 
    "job": "Computer Engineer"
    ```
    
- `Hash_value Writer`

    Sometimes, values can be added under the specific key. In this case, generated Redis key is used as `subKey` under the specified `table name`. Writer uses `redis.table` configuration for this. Writer converts Kafka record to the same format  as in the  `cache_writer`. 
    
    Example inputs data in Kafka topic;
    ```shell
    record key   : "96533" 
    record value : {"id": "125", "name": "shelly", "surname": "stark", "age": "25", "address": "address", "job": "Computer Engineer"}
    
    record key   : "12345" 
    record value : {"id": "10", "name": "jaime", "surname": "older", "age": "29", "address": "address1", "job": "Doctor"}
    ```
    
    If you set `record.table` to `redis-kafka-table` and `use.record.key` to `true`, records will be saved like this;
    
    ```shell
    redis-kafka-table:
        "96533": {"id": "125", "name": "shelly", "surname": "stark", "age": "25", "address": "address", "job": "Computer Engineer"}
        "12345": {"id": "10", "name": "jaime", "surname": "older", "age": "29", "address": "address1", "job": "Doctor"}
    ```
    You can use multiple fields as Redis key like in the other writers with specific primary key delimiter.
    

## Sink Properties

| Property      | Description    | Type | Default Value | Valid Values |
| :------------: |:---------------:| :-----:| :---------------:| :--------------:|
|redis.writer  | Type of data that will be stored in the Redis. | String | cache | cache<br/>hash_field<br/>hash_value
|redis.type  | Redis Connection Type | string | standalone | standalone<br/>sentinel
|use.record.key| If it is true, Kafka record key is used as Redis key. Otherwise, **redis.key.fields** property is used to decide Redis key. | boolean | true | true<br/>false
|redis.key.fields| Fields to decide Redis key from record value. For multiple fields, set this value as **field1:field2** format. This option is used by writers when **use.record.key** is false. | string | none | none
|redis.key.delimiter| Delimiter for selected multiple fields for Redis key. | string | . | any string values
|redis.table  | If you use **'hash_value'** to save data to Redis, you must specify the table name. This represents key in the Redis. Generated Redis records from received Kafka records will be saved under the this table. | String | sinkTable | any string values
|redis.connection.hosts| Host and port information to connect to the Redis. It must be as **host:port** format. If there is more than one host(e.g redis sentinel), it must be as **host:port,host1:port1** format. | string | localhost:6379 | none
|redis.connection.timeout| Maximum time to connect to the Redis. | int | 2000 | none
|redis.connection.password| Password of Redis. | string | null | none 
|redis.dbNum| Database number where data will be saved. | int | 0 | none


#### Example kafka-connect-redis-sink.properties
```properties
name=my-redis-sink
connector.class=connect.RedisSinkConnector
tasks.max=1
topics=test-sink

redis.writer=cache
redis.type=standalone
use.record.key=false
#redis.key.fields=fieldName
#redis.key.delimiter=,
#redis.table=tbl
redis.connection.hosts=localhost:6379
#redis.connection.password=none
#redis.connection.timeout=2000
redis.dbNum=0
```