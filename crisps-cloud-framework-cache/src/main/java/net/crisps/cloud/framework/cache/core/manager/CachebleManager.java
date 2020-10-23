package net.crisps.cloud.framework.cache.core.manager;

import net.crisps.cloud.framework.cache.core.cache.Cache;
import net.crisps.cloud.framework.cache.core.cache.CacheMe;
import net.crisps.cloud.framework.cache.core.cache.redis.RedisCache;
import net.crisps.cloud.framework.cache.core.setting.CacheSetting;
import org.springframework.data.redis.core.RedisTemplate;

public class CachebleManager extends AbstractCacheManager {
    public CachebleManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        cacheManagers.add(this);
    }

    @Override
    protected Cache getMissingCache(String name, CacheSetting cacheSetting) {
        // 创建二级缓存
        RedisCache redisCache = new RedisCache(name, redisTemplate, cacheSetting.getSecondaryCacheSetting(), getStats());
        return new CacheMe(redisTemplate, redisCache, super.getStats(), cacheSetting);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
