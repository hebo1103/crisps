package net.crisps.cloud.framework.cache.core.cache;

import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import net.crisps.cloud.framework.cache.core.listener.RedisPubSubMessage;
import net.crisps.cloud.framework.cache.core.listener.RedisPubSubMessageType;
import net.crisps.cloud.framework.cache.core.listener.RedisPublisher;
import net.crisps.cloud.framework.cache.core.setting.CacheSetting;
import net.crisps.cloud.framework.cache.core.stats.CacheStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.Objects;
import java.util.concurrent.Callable;

public class CacheMe extends AbstractValueAdaptingCache {
    Logger logger = LoggerFactory.getLogger(CacheMe.class);

    private RedisTemplate<String, Object> redisTemplate;


    private AbstractValueAdaptingCache secondCache;

    private CacheSetting cacheSetting;

    private boolean useFirstCache = true;

    public CacheMe(RedisTemplate<String, Object> redisTemplate,
                   AbstractValueAdaptingCache secondCache, boolean stats, CacheSetting cacheSetting) {
        this(redisTemplate, secondCache, true, stats, secondCache.getName(), cacheSetting);
    }

    public CacheMe(RedisTemplate<String, Object> redisTemplate,
                   AbstractValueAdaptingCache secondCache, boolean useFirstCache, boolean stats, String name, CacheSetting cacheSetting) {
        super(stats, name);
        this.redisTemplate = redisTemplate;
        this.secondCache = secondCache;
        this.useFirstCache = useFirstCache;
        this.cacheSetting = cacheSetting;
    }

    @Override
    public Object get(Object key) {
        Object result = null;
        if (useFirstCache) {
//            result = firstCache.get(key);
            logger.debug("查询一级缓存。 key={},是否命中:{}", key, Objects.nonNull(result));
        }
        if (result == null) {
            result = secondCache.get(key);
//            firstCache.putIfAbsent(key, result);
            logger.debug("查询二级缓存,并将数据放到一级缓存。 key={},是否命中:{}", key, Objects.nonNull(result));
        }
        return fromStoreValue(result);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
//        if (useFirstCache) {
//            Object result = firstCache.get(key, type);
//            logger.debug("查询一级缓存。 key={},是否命中:{}", key, Objects.nonNull(result));
//            if (result != null) {
//                return (T) fromStoreValue(result);
//            }
//        }

        T result = secondCache.get(key, type);
//        firstCache.putIfAbsent(key, result);
        logger.debug("查询二级缓存,并将数据放到一级缓存。 key={},是否命中:{}", key, Objects.nonNull(result));
        return result;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object object = JdHotKeyStore.getValue(String.valueOf(key));
        if (object != null) {
            logger.debug("热点key,查询本地缓存。 key={}", key);
            return (T) fromStoreValue(object);
        }
        T result = secondCache.get(key, valueLoader);
        JdHotKeyStore.smartSet(String.valueOf(key), result);
        logger.debug("非热点key,查询redis缓存。 key={}", key);
        return result;
    }

    @Override
    public void put(Object key, Object value) {
        secondCache.put(key, value);
        // 删除一级缓存
        if (useFirstCache) {
            deleteFirstCache(key);
        }
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        Object result = secondCache.putIfAbsent(key, value);
        // 删除一级缓存
        if (useFirstCache) {
            deleteFirstCache(key);
        }
        return result;
    }

    @Override
    public void evict(Object key) {
        // 删除的时候要先删除二级缓存再删除一级缓存，否则有并发问题
        secondCache.evict(key);
        // 删除一级缓存
        if (JdHotKeyStore.isHotKey(String.valueOf(key))) {
            deleteFirstCache(key);
        }
    }

    @Override
    public void clear() {
        // 删除的时候要先删除二级缓存再删除一级缓存，否则有并发问题
        secondCache.clear();
        if (useFirstCache) {
            // 清除一级缓存需要用到redis的订阅/发布模式，否则集群中其他服服务器节点的一级缓存数据无法删除
            RedisPubSubMessage message = new RedisPubSubMessage();
            message.setCacheName(getName());
            message.setMessageType(RedisPubSubMessageType.CLEAR);
            // 发布消息
            RedisPublisher.publisher(redisTemplate, new ChannelTopic(getName()), message);
        }
    }

    @Override
    public void outherClear() {
        logger.info("订阅消息清空缓存：{}", getName());
        // 删除的时候要先删除二级缓存再删除一级缓存，否则有并发问题
        secondCache.clear();
//        if (useFirstCache) {
//            com.github.benmanes.caffeine.cache.Cache cache = (com.github.benmanes.caffeine.cache.Cache) getFirstCache().getNativeCache();
//            Map concurrentMap = cache.asMap();
//            Iterator<Map.Entry<String, Object>> entries = concurrentMap.entrySet().iterator();
//            List<String> keys = new ArrayList<>();
//            while (entries.hasNext()) {
//                Map.Entry<String, Object> entry = entries.next();
//                String key = entry.getKey();
//                keys.add(key);
//            }
//            Pattern pattern = Pattern.compile(getName());
//            for(int i = 0; i<keys.size(); i++) {
//                Matcher matcher = pattern.matcher(keys.get(i));
//                if (matcher.find()) {
//                   firstCache.evict(keys.get(i));
//                }
//            }
//        }
    }

    private void deleteFirstCache(Object key) {
        logger.info("清除本地缓存 key= {} ", key);
        JdHotKeyStore.remove(String.valueOf(key));
//        // 删除一级缓存需要用到redis的Pub/Sub（订阅/发布）模式，否则集群中其他服服务器节点的一级缓存数据无法删除
//        RedisPubSubMessage message = new RedisPubSubMessage();
//        message.setCacheName(getName());
//        message.setKey(key);
//        message.setMessageType(RedisPubSubMessageType.EVICT);
//        // 发布消息
//        RedisPublisher.publisher(redisTemplate, new ChannelTopic(getName()), message);
    }

    public Cache getSecondCache() {
        return secondCache;
    }

    @Override
    public CacheStats getCacheStats() {
        CacheStats cacheStats = new CacheStats();
        cacheStats.addCachedMethodRequestCount(secondCache.getCacheStats().getCachedMethodRequestCount().longValue());
        cacheStats.addCachedMethodRequestTime(secondCache.getCacheStats().getCachedMethodRequestTime().longValue());
        setCacheStats(cacheStats);
        return cacheStats;
    }

    public CacheSetting getCacheSetting() {
        return cacheSetting;
    }

    @Override
    public boolean isAllowNullValues() {
        return secondCache.isAllowNullValues();
    }
}
