package net.crisps.cloud.framework.cache.core.cache;

import net.crisps.cloud.framework.cache.core.stats.CacheStats;

import java.util.concurrent.Callable;

public interface Cache {

    String getName();

    Object get(Object key);

    <T> T get(Object key, Class<T> type);

    <T> T get(Object key, Callable<T> valueLoader);

    void put(Object key, Object value);

    Object putIfAbsent(Object key, Object value);

    void evict(Object key);

    void clear();

    void outherClear();

    CacheStats getCacheStats();
}
