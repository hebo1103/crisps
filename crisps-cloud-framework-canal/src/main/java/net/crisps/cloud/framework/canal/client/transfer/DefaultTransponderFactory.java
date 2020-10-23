package net.crisps.cloud.framework.canal.client.transfer;

import com.alibaba.otter.canal.client.CanalConnector;
import net.crisps.cloud.framework.canal.client.ListenerPoint;
import net.crisps.cloud.framework.canal.event.CanalEventListener;
import net.crisps.cloud.framework.canal.config.CanalConfig;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public class DefaultTransponderFactory implements TransponderFactory {
    @Override
    public MessageTransponder newTransponder(CanalConnector connector, Map.Entry<String, CanalConfig.Instance> config, List<CanalEventListener> listeners,
                                             List<ListenerPoint> annoListeners) {
        return new DefaultMessageTransponder(connector, config, listeners, annoListeners);
    }
}
