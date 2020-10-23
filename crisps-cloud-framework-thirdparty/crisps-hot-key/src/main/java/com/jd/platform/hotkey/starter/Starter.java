package com.jd.platform.hotkey.starter;
import com.jd.platform.hotkey.client.ClientStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Administrator
 */
@Component
@Slf4j
public class Starter {
    @Value("${dc.etcd.server}")
    private String etcd;
    @Value("${dc.name}")
    private String appName;

    @PostConstruct
    public void init() {
        ClientStarter.Builder builder = new ClientStarter.Builder();
        ClientStarter starter = builder.setAppName(appName).setEtcdServer(etcd).build();
        starter.startPipeline();
        log.info("hotkey启动成功");
    }
}
