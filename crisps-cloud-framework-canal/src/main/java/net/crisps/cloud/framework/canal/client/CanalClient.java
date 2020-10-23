package net.crisps.cloud.framework.canal.client;

/**
 * @author Administrator
 */
public interface CanalClient {

    void start();

    void stop();

    boolean isRunning();
}
