package net.crisps.cloud.framework.cache.core.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

/**
 * @author Administrator
 */
public class RedisPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RedisPublisher.class);

    private RedisPublisher() {

    }

    public static void publisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic channelTopic, Object message) {
        redisTemplate.convertAndSend(channelTopic.toString(), message);
        logger.debug("redis消息发布者向频道【{}】发布了【{}】消息", channelTopic.toString(), message.toString());
    }
}
