package net.crisps.cloud.framework.canal.client.transfer;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import net.crisps.cloud.framework.canal.client.ListenerPoint;
import net.crisps.cloud.framework.canal.client.exception.CanalClientException;
import net.crisps.cloud.framework.canal.event.CanalEventListener;
import net.crisps.cloud.framework.canal.annotation.ListenPoint;
import net.crisps.cloud.framework.canal.config.CanalConfig;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Administrator
 */
public abstract class AbstractBasicMessageTransponder extends AbstractMessageTransponder {

    public AbstractBasicMessageTransponder(CanalConnector connector, Map.Entry<String, CanalConfig.Instance> config, List<CanalEventListener> listeners, List<ListenerPoint> annoListeners) {
        super(connector, config, listeners, annoListeners);
    }

    @Override
    protected void distributeEvent(Message message) {
        List<CanalEntry.Entry> entries = message.getEntries();
        for (CanalEntry.Entry entry : entries) {
            List<CanalEntry.EntryType> ignoreEntryTypes = getIgnoreEntryTypes();
            if (ignoreEntryTypes != null
                    && ignoreEntryTypes.stream().anyMatch(t -> entry.getEntryType() == t)) {
                continue;
            }
            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new CanalClientException("解析ERROR , data:" + entry.toString(),e);
            }
            if (rowChange.hasIsDdl() && rowChange.getIsDdl()) {
                processDdl(rowChange);
                continue;
            }
            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                distributeByImpl(rowChange.getEventType(), rowData);
                distributeByAnnotation(destination,
                        entry.getHeader().getSchemaName(),
                        entry.getHeader().getTableName(),
                        rowChange.getEventType(),
                        rowData);
            }
        }
    }

    protected void processDdl(CanalEntry.RowChange rowChange) {}

    protected void distributeByImpl(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        if (listeners != null) {
            for (CanalEventListener listener : listeners) {
                listener.onEvent(eventType, rowData);
            }
        }
    }

    protected void distributeByAnnotation(String destination,
                                        String schemaName,
                                        String tableName,
                                        CanalEntry.EventType eventType,
                                        CanalEntry.RowData rowData) {
        annoListeners.forEach(point -> point
                .getInvokeMap()
                .entrySet()
                .stream()
                .filter(getAnnotationFilter(destination, schemaName, tableName, eventType))
                .forEach(entry -> {
                    Method method = entry.getKey();
                    method.setAccessible(true);
                    try {
                        Object[] args = getInvokeArgs(method, eventType, rowData);
                        method.invoke(point.getTarget(), args);
                    } catch (Exception e) {
                    }
                }));
    }

    protected abstract Predicate<Map.Entry<Method, ListenPoint>> getAnnotationFilter(String destination,
                                                                            String schemaName,
                                                                            String tableName,
                                                                            CanalEntry.EventType eventType);

    protected abstract Object[] getInvokeArgs(Method method, CanalEntry.EventType eventType,
                                              CanalEntry.RowData rowData);

    protected List<CanalEntry.EntryType> getIgnoreEntryTypes() {
        return Collections.emptyList();
    }

}
