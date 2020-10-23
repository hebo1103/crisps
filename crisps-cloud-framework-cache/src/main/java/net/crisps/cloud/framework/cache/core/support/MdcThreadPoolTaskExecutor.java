package net.crisps.cloud.framework.cache.core.support;

import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

public class MdcThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @Override
    public void execute(Runnable runnable) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        super.execute(() -> run(runnable, context));
    }

    private void run(Runnable runnable, Map<String, String> context) {
        // 将父线程的MDC内容传给子线程
        if (context != null) {
            try {
                MDC.setContextMap(context);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        try {
            // 执行异步操作
            runnable.run();
        } finally {
            // 清空MDC内容
            MDC.clear();
        }
    }
}
