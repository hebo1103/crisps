package net.crisps.cloud.framework.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import net.crisps.cloud.framework.canal.client.exception.CanalClientException;
import net.crisps.cloud.framework.canal.client.transfer.TransponderFactory;
import net.crisps.cloud.framework.canal.config.CanalConfig;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;

/**
 * @author Administrator
 */
public abstract class AbstractCanalClient implements CanalClient {

    private volatile boolean running;

    private CanalConfig canalConfig;


    protected final TransponderFactory factory;

    AbstractCanalClient(CanalConfig canalConfig, TransponderFactory factory) {
        Objects.requireNonNull(canalConfig, "canalConfig can not be null!");
        Objects.requireNonNull(canalConfig, "transponderFactory can not be null!");
        this.canalConfig = canalConfig;
        this.factory = factory;
    }

    @Override
    public void start() {
        Map<String, CanalConfig.Instance> instanceMap = getConfig();
        for (Map.Entry<String, CanalConfig.Instance> instanceEntry : instanceMap.entrySet()) {
            process(processInstanceEntry(instanceEntry), instanceEntry);
        }

    }

    protected abstract void process(CanalConnector connector, Map.Entry<String, CanalConfig.Instance> config);

    private CanalConnector processInstanceEntry(Map.Entry<String, CanalConfig.Instance> instanceEntry) {
        CanalConfig.Instance instance = instanceEntry.getValue();
        CanalConnector connector;
        if (instance.isClusterEnabled()) {
            connector = CanalConnectors.newClusterConnector(instance.getZookeeperAddress(), instanceEntry.getKey(), instance.getUserName(), instance.getPassword());
        } else {
            connector = CanalConnectors.newSingleConnector(new InetSocketAddress(instance.getHost(), instance.getPort()),
                    instanceEntry.getKey(),
                    instance.getUserName(),
                    instance.getPassword());
        }
        connector.connect();
        if (!StringUtils.isEmpty(instance.getFilter())) {
            connector.subscribe(instance.getFilter());
        } else {
            connector.subscribe();
        }

        connector.rollback();
        return connector;
    }

    protected Map<String, CanalConfig.Instance> getConfig() {
        CanalConfig config = canalConfig;
        Map<String, CanalConfig.Instance> instanceMap;
        if (config != null && (instanceMap = config.getInstances()) != null && !instanceMap.isEmpty()) {
            return config.getInstances();
        } else {
            throw new CanalClientException("can not get the configuration of canal client!");
        }
    }

    @Override
    public void stop() {
        setRunning(false);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }
}
