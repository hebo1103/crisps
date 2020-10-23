package net.crisps.cloud.framework.cache.core.util;

import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.HashSet;
import java.util.Set;

/**
 * redis扩展工具
 */
public abstract class RedisHelper {
    public static Set<String> scan(RedisTemplate<String, Object> redisTemplate, String pattern) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            if (connection instanceof RedisClusterConnection) {
                //集群模式
                return redisTemplate.keys(pattern);
            }

            // 单机模式
            Set<String> keysTmp = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder()
                    .match(pattern)
                    .count(10000).build())) {

                while (cursor.hasNext()) {
                    keysTmp.add(new String(cursor.next(), "Utf-8"));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return keysTmp;
        });
    }
}

