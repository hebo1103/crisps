package net.crisps.cloud.framework.cache.core.stats;

import net.crisps.cloud.framework.cache.core.setting.CacheSetting;

import java.io.Serializable;

public class CacheStatsInfo implements Serializable {

    private String cacheName;
    private String internalKey;

    private String depict;

    private long requestCount;

    private long missCount;

    private double hitRate;

    private long firstCacheRequestCount;
    private long firstCacheMissCount;
    private long secondCacheRequestCount;

    private long secondCacheMissCount;
    private long totalLoadTime;
    private CacheSetting cacheSetting;


    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public long getMissCount() {
        return missCount;
    }

    public void setMissCount(long missCount) {
        this.missCount = missCount;
    }

    public long getFirstCacheRequestCount() {
        return firstCacheRequestCount;
    }

    public void setFirstCacheRequestCount(long firstCacheRequestCount) {
        this.firstCacheRequestCount = firstCacheRequestCount;
    }

    public long getFirstCacheMissCount() {
        return firstCacheMissCount;
    }

    public void setFirstCacheMissCount(long firstCacheMissCount) {
        this.firstCacheMissCount = firstCacheMissCount;
    }

    public long getSecondCacheRequestCount() {
        return secondCacheRequestCount;
    }

    public void setSecondCacheRequestCount(long secondCacheRequestCount) {
        this.secondCacheRequestCount = secondCacheRequestCount;
    }

    public long getSecondCacheMissCount() {
        return secondCacheMissCount;
    }

    public void setSecondCacheMissCount(long secondCacheMissCount) {
        this.secondCacheMissCount = secondCacheMissCount;
    }

    public long getTotalLoadTime() {
        return totalLoadTime;
    }

    public void setTotalLoadTime(long totalLoadTime) {
        this.totalLoadTime = totalLoadTime;
    }

    public String getInternalKey() {
        return internalKey;
    }

    public void setInternalKey(String internalKey) {
        this.internalKey = internalKey;
    }

    public CacheSetting getCacheSetting() {
        return cacheSetting;
    }

    public void setCacheSetting(CacheSetting cacheSetting) {
        this.cacheSetting = cacheSetting;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }

    public double getHitRate() {
        return hitRate;
    }

    public void setHitRate(double hitRate) {
        this.hitRate = hitRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CacheStatsInfo that = (CacheStatsInfo) o;

        if (cacheName != null ? !cacheName.equals(that.cacheName) : that.cacheName != null) {
            return false;
        }
        return internalKey != null ? internalKey.equals(that.internalKey) : that.internalKey == null;
    }

    @Override
    public int hashCode() {
        int result = cacheName != null ? cacheName.hashCode() : 0;
        result = 31 * result + (internalKey != null ? internalKey.hashCode() : 0);
        return result;
    }

    /**
     * 清空统计信息
     */
    public void clearStatsInfo() {
        this.setRequestCount(0);
        this.setMissCount(0);
        this.setTotalLoadTime(0);
        this.setHitRate(0);

        this.setFirstCacheRequestCount(0);
        this.setFirstCacheMissCount(0);

        this.setSecondCacheRequestCount(0);
        this.setSecondCacheMissCount(0);
    }
}
