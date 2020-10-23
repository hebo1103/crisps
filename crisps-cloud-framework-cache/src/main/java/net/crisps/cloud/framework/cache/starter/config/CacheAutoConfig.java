package net.crisps.cloud.framework.cache.starter.config;

import lombok.extern.slf4j.Slf4j;
import net.crisps.cloud.framework.cache.aspectj.aspect.CacheAspect;
import net.crisps.cloud.framework.cache.core.manager.CacheManager;
import net.crisps.cloud.framework.cache.core.manager.CachebleManager;
import net.crisps.cloud.framework.cache.core.serializer.KryoRedisSerializer;
import net.crisps.cloud.framework.cache.core.serializer.StringRedisSerializer;
import net.dgg.framework.tac.redis.bean.DggRedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@Configuration
@ConditionalOnBean(RedisTemplate.class)
@AutoConfigureAfter({RedisAutoConfiguration.class})
@EnableAspectJAutoProxy
@Slf4j
public class CacheAutoConfig {


    @Resource
    DggRedisUtils dggRedisUtils;

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager cacheManager() {
        dggRedisUtils.setValueSerializer(new KryoRedisSerializer(Object.class));
        CachebleManager cachebleManager = new CachebleManager(dggRedisUtils.getRedisTemplate());
        // 默认开启统计功能
        cachebleManager.setStats(false);
        log.info("********开启Cache**********");
        return cachebleManager;
    }

    @Bean
    public CacheAspect portalCacheAspect() {
        return new CacheAspect();
    }

}
