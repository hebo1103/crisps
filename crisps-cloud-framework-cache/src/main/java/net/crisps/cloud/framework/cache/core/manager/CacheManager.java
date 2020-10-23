package net.crisps.cloud.framework.cache.core.manager;

import net.crisps.cloud.framework.cache.core.cache.Cache;
import net.crisps.cloud.framework.cache.core.setting.CacheSetting;
import net.crisps.cloud.framework.cache.core.stats.CacheStatsInfo;

import java.util.Collection;
import java.util.List;

public interface CacheManager {

    Collection<Cache> getCache(String name);

    Cache getCache(String name, CacheSetting cacheSetting);

    Collection<String> getCacheNames();

    List<CacheStatsInfo> listCacheStats(String cacheName);

    void resetCacheStat();

}
