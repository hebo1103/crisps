package net.crisps.cloud.framework.canal.client.transfer;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import net.crisps.cloud.framework.canal.client.ListenerPoint;
import net.crisps.cloud.framework.canal.event.CanalEventListener;
import net.crisps.cloud.framework.canal.config.CanalConfig;

import java.util.*;

/**
 * @author Administrator
 */
public abstract class AbstractMessageTransponder implements MessageTransponder {

    private final CanalConnector connector;

    protected final CanalConfig.Instance config;

    protected final String destination;

    protected final List<CanalEventListener> listeners = new ArrayList<>();

    protected final List<ListenerPoint> annoListeners = new ArrayList<>();

    private volatile boolean running = true;


    public AbstractMessageTransponder(CanalConnector connector,
                                      Map.Entry<String, CanalConfig.Instance> config,
                                      List<CanalEventListener> listeners,
                                      List<ListenerPoint> annoListeners) {
        Objects.requireNonNull(connector, "connector can not be null!");
        Objects.requireNonNull(config, "config can not be null!");
        this.connector = connector;
        this.destination = config.getKey();
        this.config = config.getValue();
        if (listeners != null){
            this.listeners.addAll(listeners);
        }
        if (annoListeners != null){
            this.annoListeners.addAll(annoListeners);
        }
    }

    @Override
    public void run() {
        int errorCount = config.getRetryCount();
        final long interval = config.getAcquireInterval();
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Message message = connector.getWithoutAck(config.getBatchSize());
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    Thread.sleep(interval);
                } else {
                    distributeEvent(message);
                }
                connector.ack(batchId);
            } catch (CanalClientException e) {
                errorCount--;
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e1) {
                    errorCount = 0;
                }
            } catch (InterruptedException e) {
                errorCount = 0;
                connector.rollback();
            } finally {
                if (errorCount <= 0) {
                    stop();
                }
            }
        }
        stop();
    }

    protected abstract void distributeEvent(Message message);

    void stop() {
        running = false;
    }

}
