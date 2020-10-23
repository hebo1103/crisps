package net.crisps.cloud.framework.cache.core.cache.redis;

import net.crisps.cloud.framework.cache.core.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;

public class RedisCacheKey {

    /**
     * 前缀序列化器
     */
    private final RedisSerializer prefixSerializer1 = new StringRedisSerializer();

    /**
     * 缓存key
     */
    private final Object keyElement;

    /**
     * 缓存名称
     */
    private String cacheName;

    /**
     * 是否使用缓存前缀
     */
    private boolean usePrefix = true;

    /**
     * RedisTemplate 的key序列化器
     */
    private final RedisSerializer serializer;

    /**
     * @param keyElement 缓存key
     * @param serializer RedisSerializer
     */
    public RedisCacheKey(Object keyElement, RedisSerializer serializer) {

        Assert.notNull(keyElement, "缓存key不能为NULL");
        Assert.notNull(serializer, "key的序列化器不能为NULL");
        this.keyElement = keyElement;
        this.serializer = serializer;
    }

    public String getKey() {

        return new String(getKeyBytes());
    }

    public byte[] getKeyBytes() {

        byte[] rawKey = serializeKeyElement();
        if (!usePrefix) {
            return rawKey;
        }
        byte[] prefix = getPrefix();
        byte[] prefixedKey = Arrays.copyOf(prefix, prefix.length + rawKey.length);
        System.arraycopy(rawKey, 0, prefixedKey, prefix.length, rawKey.length);

        return prefixedKey;
    }

    private byte[] serializeKeyElement() {

        if (serializer == null && keyElement instanceof byte[]) {
            return (byte[]) keyElement;
        }

        return serializer.serialize(keyElement);
    }

    public byte[] getPrefix() {
        return prefixSerializer1.serialize((StringUtils.isEmpty(cacheName) ? cacheName.concat(":") : cacheName.concat(":")));
    }

    public RedisCacheKey cacheName(String cacheName) {
        this.cacheName = cacheName;
        return this;
    }

    public RedisCacheKey usePrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
        return this;
    }

}
