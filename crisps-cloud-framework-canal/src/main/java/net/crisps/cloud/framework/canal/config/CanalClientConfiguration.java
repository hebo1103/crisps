package net.crisps.cloud.framework.canal.config;


import net.crisps.cloud.framework.canal.client.CanalClient;
import net.crisps.cloud.framework.canal.client.SimpleCanalClient;
import net.crisps.cloud.framework.canal.client.transfer.MessageTransponders;
import net.crisps.cloud.framework.canal.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;


public class CanalClientConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(CanalClientConfiguration.class);

    @Autowired
    private CanalConfig canalConfig;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public BeanUtil beanUtil() {
        return new BeanUtil();
    }

    @Bean
    private CanalClient canalClient() {
        CanalClient canalClient = new SimpleCanalClient(canalConfig, MessageTransponders.defaultMessageTransponder());
        canalClient.start();
        logger.info("Starting canal client....");
        return canalClient;
    }
}
