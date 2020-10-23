package net.crisps.cloud.framework.canal.event;

import com.alibaba.otter.canal.protocol.CanalEntry;

/**
 * @author Administrator
 */
public interface CanalEventListener {

    void onEvent(CanalEntry.EventType eventType, CanalEntry.RowData rowData);

}
