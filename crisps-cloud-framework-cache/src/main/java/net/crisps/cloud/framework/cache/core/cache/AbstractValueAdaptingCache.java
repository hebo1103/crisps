package net.crisps.cloud.framework.cache.core.cache;
import com.alibaba.fastjson.JSON;
import net.crisps.cloud.framework.cache.core.stats.CacheStats;
import net.crisps.cloud.framework.cache.core.support.NullValue;
import org.springframework.util.Assert;


public abstract class AbstractValueAdaptingCache implements Cache {

    /**
     * 缓存名称
     */
    private final String name;

    private boolean stats;

    private CacheStats cacheStats = new CacheStats();
    protected AbstractValueAdaptingCache(boolean stats, String name) {
        Assert.notNull(name, "缓存名称不能为NULL");
        this.stats = stats;
        this.name = name;
    }

    public abstract boolean isAllowNullValues();

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        return (T) fromStoreValue(get(key));
    }

    protected Object fromStoreValue(Object storeValue) {
        if (isAllowNullValues() && storeValue instanceof NullValue) {
            return null;
        }
        return storeValue;
    }

    protected Object toStoreValue(Object userValue) {
        if (isAllowNullValues() && userValue == null) {
            return NullValue.INSTANCE;
        }
        return userValue;
    }


    public class LoaderCacheValueException extends RuntimeException {

        private final Object key;

        public LoaderCacheValueException(Object key, Throwable ex) {
            super(String.format("加载key为 %s 的缓存数据,执行被缓存方法异常", JSON.toJSONString(key)), ex);
            this.key = key;
        }

        public Object getKey() {
            return this.key;
        }
    }

    public boolean isStats() {
        return stats;
    }

    @Override
    public CacheStats getCacheStats() {
        return cacheStats;
    }

    public void setCacheStats(CacheStats cacheStats) {
        this.cacheStats = cacheStats;
    }
}
