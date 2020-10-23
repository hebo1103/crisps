package net.crisps.cloud.framework.cache.core.setting;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class SecondaryCacheSetting implements Serializable {
    private long expiration = 0;

    private long preloadTime = 0;

    private TimeUnit timeUnit = TimeUnit.MICROSECONDS;
    private boolean forceRefresh = false;

    private boolean usePrefix = true;

    boolean allowNullValue = false;

    int magnification = 1;

    public SecondaryCacheSetting() {
    }

    public SecondaryCacheSetting(long expiration, long preloadTime, TimeUnit timeUnit, boolean forceRefresh,
                                 boolean allowNullValues, int magnification) {
        this.expiration = expiration;
        this.preloadTime = preloadTime;
        this.timeUnit = timeUnit;
        this.forceRefresh = forceRefresh;
        this.allowNullValue = allowNullValues;
        this.magnification = magnification;
        this.usePrefix = true;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getPreloadTime() {
        return preloadTime;
    }

    public void setPreloadTime(long preloadTime) {
        this.preloadTime = preloadTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public boolean isForceRefresh() {
        return forceRefresh;
    }

    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
    }

    public boolean isUsePrefix() {
        return usePrefix;
    }

    public void setUsePrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    public boolean isAllowNullValue() {
        return allowNullValue;
    }

    public void setAllowNullValue(boolean allowNullValue) {
        this.allowNullValue = allowNullValue;
    }

    public int getMagnification() {
        return magnification;
    }

    public void setMagnification(int magnification) {
        this.magnification = magnification;
    }
}
