package net.crisps.cloud.framework.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import net.crisps.cloud.framework.canal.event.CanalEventListener;
import net.crisps.cloud.framework.canal.annotation.ListenPoint;
import net.crisps.cloud.framework.canal.client.transfer.TransponderFactory;
import net.crisps.cloud.framework.canal.config.CanalConfig;
import net.crisps.cloud.framework.canal.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
public class SimpleCanalClient extends AbstractCanalClient {

    /**
     * executor
     */
    private ThreadPoolExecutor executor;

    private final List<CanalEventListener> listeners = new ArrayList<>();

    private final List<ListenerPoint> annoListeners = new ArrayList<>();

    private final static Logger logger = LoggerFactory.getLogger(SimpleCanalClient.class);

    public SimpleCanalClient(CanalConfig canalConfig, TransponderFactory factory) {
        super(canalConfig, factory);
        executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), Executors.defaultThreadFactory());
        initListeners();
    }

    @Override
    protected void process(CanalConnector connector, Map.Entry<String, CanalConfig.Instance> config) {
        executor.submit(factory.newTransponder(connector, config, listeners, annoListeners));
    }

    @Override
    public void stop() {
        super.stop();
        executor.shutdown();
    }

    private void initListeners() {
        logger.info("{}: 初始化Canal-listeners....", Thread.currentThread().getName());
        List<CanalEventListener> list = BeanUtil.getBeansOfType(CanalEventListener.class);
        if (list != null) {
            listeners.addAll(list);
        }
        Map<String, Object> listenerMap = BeanUtil.getBeansWithAnnotation( net.crisps.cloud.framework.canal.annotation.CanalEventListener.class);
        if (listenerMap != null) {
            for (Object target : listenerMap.values()) {
                Method[] methods = target.getClass().getDeclaredMethods();
                if (methods != null && methods.length > 0) {
                    for (Method method : methods) {
                        ListenPoint l = AnnotationUtils.findAnnotation(method, ListenPoint.class);
                        if (l != null) {
                            annoListeners.add(new ListenerPoint(target, method, l));
                        }
                    }
                }
            }
        }
    }
}
