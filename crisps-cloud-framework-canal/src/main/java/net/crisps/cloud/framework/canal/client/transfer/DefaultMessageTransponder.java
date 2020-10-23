package net.crisps.cloud.framework.canal.client.transfer;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import net.crisps.cloud.framework.canal.client.ListenerPoint;
import net.crisps.cloud.framework.canal.event.CanalEventListener;
import net.crisps.cloud.framework.canal.annotation.ListenPoint;
import net.crisps.cloud.framework.canal.config.CanalConfig;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Administrator
 */
public class DefaultMessageTransponder extends AbstractBasicMessageTransponder {

    public DefaultMessageTransponder(CanalConnector connector,
                          Map.Entry<String, CanalConfig.Instance> config,
                          List<CanalEventListener> listeners,
                          List<ListenerPoint> annoListeners) {
        super(connector, config, listeners, annoListeners);
    }

    @Override
    protected Predicate<Map.Entry<Method, ListenPoint>> getAnnotationFilter(String destination,
                                                                            String schemaName,
                                                                            String tableName,
                                                                            CanalEntry.EventType eventType) {
        Predicate<Map.Entry<Method, ListenPoint>> df = e -> StringUtils.isEmpty(e.getValue().destination())
                || e.getValue().destination().equals(destination);
        Predicate<Map.Entry<Method, ListenPoint>> sf = e -> e.getValue().schema().length == 0
                || Arrays.stream(e.getValue().schema()).anyMatch(s -> s.equals(schemaName));
        Predicate<Map.Entry<Method, ListenPoint>> tf = e -> e.getValue().table().length == 0
                || Arrays.stream(e.getValue().table()).anyMatch(t -> t.equals(tableName));
        Predicate<Map.Entry<Method, ListenPoint>> ef = e -> e.getValue().eventType().length == 0
                || Arrays.stream(e.getValue().eventType()).anyMatch(ev -> ev == eventType);
        return df.and(sf).and(tf).and(ef);
    }

    @Override
    protected Object[] getInvokeArgs(Method method, CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        return Arrays.stream(method.getParameterTypes())
                .map(p -> p == CanalEntry.EventType.class
                        ? eventType
                        : p == CanalEntry.RowData.class
                        ? rowData : null)
                .toArray();
    }

    @Override
    protected List<CanalEntry.EntryType> getIgnoreEntryTypes() {
        return Arrays.asList(CanalEntry.EntryType.TRANSACTIONBEGIN, CanalEntry.EntryType.TRANSACTIONEND, CanalEntry.EntryType.HEARTBEAT);
    }
}
