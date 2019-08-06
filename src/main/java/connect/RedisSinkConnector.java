package connect;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RedisSinkConnector extends SinkConnector {
    private static final Logger log = LoggerFactory.getLogger(RedisSinkConnector.class);

    private Map<String, String> configProps;


    @Override
    public Class<? extends Task> taskClass() {
        return RedisSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        log.trace("maxTasks = {}, taskConfigs = {}", maxTasks, configProps);

        final List<Map<String, String>> configs = new ArrayList<>(maxTasks);
        for (int i = 0; i < maxTasks; ++i) {
            configs.add(configProps);
        }
        return configs;
    }

    @Override
    public void start(Map<String, String> props) {
        configProps = props;
        log.trace("Connector is starting with props = {}", props);
    }

    @Override
    public void stop() {
        log.trace("Connector is stopping.");
    }

    @Override
    public ConfigDef config() {
        return RedisSinkConfig.CONFIG_DEF;
    }

    public String version() {
        return VersionUtil.getVersion();
    }
}
