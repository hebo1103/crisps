package net.crisps.cloud.framework.cache.core.support;

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadTaskUtils {
    private static MdcThreadPoolTaskExecutor taskExecutor = null;

    static {
        taskExecutor = new MdcThreadPoolTaskExecutor();
        // 核心线程数
        taskExecutor.setCorePoolSize(16);
        // 最大线程数
        taskExecutor.setMaxPoolSize(128);
        // 队列最大长度
        taskExecutor.setQueueCapacity(1000);
        // 线程池维护线程所允许的空闲时间(单位秒)
        taskExecutor.setKeepAliveSeconds(120);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        taskExecutor.initialize();
    }

    public static void run(Runnable runnable) {
        taskExecutor.execute(runnable);
    }
}
