package net.crisps.cloud.framework.cache.core.stats;

import com.alibaba.fastjson.JSON;
import net.crisps.cloud.framework.cache.core.cache.Cache;
import net.crisps.cloud.framework.cache.core.cache.CacheMe;
import net.crisps.cloud.framework.cache.core.manager.AbstractCacheManager;
import net.crisps.cloud.framework.cache.core.manager.CacheManager;
import net.crisps.cloud.framework.cache.core.util.RedisHelper;
import net.crisps.cloud.framework.cache.core.util.StringUtils;
import net.crisps.cloud.framework.cache.core.setting.CacheSetting;
import net.crisps.cloud.framework.cache.core.support.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StatsService {
    private static Logger logger = LoggerFactory.getLogger(StatsService.class);

    public static final String CACHE_STATS_KEY_PREFIX = "portal_cache:cache_stats_info:prortal:";

    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(50);

    private AbstractCacheManager cacheManager;

    public List<CacheStatsInfo> listCacheStats(String cacheNameParam) {
        Set<String> cacheKeys = RedisHelper.scan(cacheManager.getRedisTemplate(), CACHE_STATS_KEY_PREFIX + "*");
        if (CollectionUtils.isEmpty(cacheKeys)) {
            return Collections.emptyList();
        }
        // 遍历找出对应统计数据
        List<CacheStatsInfo> statsList = new ArrayList<>();
        for (String key : cacheKeys) {
            if (StringUtils.isNotBlank(cacheNameParam) && !key.startsWith(CACHE_STATS_KEY_PREFIX + cacheNameParam)) {
                continue;
            }

            CacheStatsInfo cacheStats = (CacheStatsInfo) cacheManager.getRedisTemplate().opsForValue().get(key);
            if (!Objects.isNull(cacheStats)) {
                statsList.add(cacheStats);
            }
        }

        return statsList.stream().sorted(Comparator.comparing(CacheStatsInfo::getHitRate)).collect(Collectors.toList());
    }

    /**
     * 同步缓存统计list
     */
    public void syncCacheStats() {
        RedisTemplate<String, Object> redisTemplate = cacheManager.getRedisTemplate();
        // 清空统计数据
        resetCacheStat();
        executor.scheduleWithFixedDelay(() -> {
            Set<AbstractCacheManager> cacheManagers = AbstractCacheManager.getCacheManager();
            for (AbstractCacheManager abstractCacheManager : cacheManagers) {
                // 获取CacheManager
                CacheManager cacheManager = abstractCacheManager;
                Collection<String> cacheNames = cacheManager.getCacheNames();
                for (String cacheName : cacheNames) {
                    // 获取Cache
                    Collection<Cache> caches = cacheManager.getCache(cacheName);
                    for (Cache cache : caches) {
                        CacheMe cacheMe = (CacheMe) cache;
                        CacheSetting cacheSetting = cacheMe.getCacheSetting();
                        // 加锁并增量缓存统计数据，缓存key=固定前缀 +缓存名称加 + 内部缓存名
                        String redisKey = CACHE_STATS_KEY_PREFIX + cacheName + cacheSetting.getInternalKey();
                        Lock lock = new Lock(redisTemplate, redisKey, 60, 5000);
                        try {
                            if (lock.tryLock()) {
                                CacheStatsInfo cacheStats = (CacheStatsInfo) redisTemplate.opsForValue().get(redisKey);
                                if (Objects.isNull(cacheStats)) {
                                    cacheStats = new CacheStatsInfo();
                                }

                                // 设置缓存唯一标示
                                cacheStats.setCacheName(cacheName);
                                cacheStats.setInternalKey(cacheSetting.getInternalKey());

                                cacheStats.setDepict(cacheSetting.getDepict());
                                // 设置缓存配置信息
                                cacheStats.setCacheSetting(cacheSetting);

                                // 设置缓存统计数据
                                CacheStats cacheStats1 = cacheMe.getCacheStats();
//                                CacheStats firstCacheStats = cacheMe.getFirstCache().getCacheStats();
                                CacheStats secondCacheStats = cacheMe.getSecondCache().getCacheStats();

                                // 清空加载缓存时间
//                                firstCacheStats.getAndResetCachedMethodRequestTime();
                                secondCacheStats.getAndResetCachedMethodRequestTime();

                                cacheStats.setRequestCount(cacheStats.getRequestCount() + cacheStats1.getAndResetCacheRequestCount());
                                cacheStats.setMissCount(cacheStats.getMissCount() + cacheStats1.getAndResetCachedMethodRequestCount());
                                cacheStats.setTotalLoadTime(cacheStats.getTotalLoadTime() + cacheStats1.getAndResetCachedMethodRequestTime());
                                cacheStats.setHitRate((cacheStats.getRequestCount() - cacheStats.getMissCount()) / (double) cacheStats.getRequestCount() * 100);

//                                cacheStats.setFirstCacheRequestCount(cacheStats.getFirstCacheRequestCount() + firstCacheStats.getAndResetCacheRequestCount());
//                                cacheStats.setFirstCacheMissCount(cacheStats.getFirstCacheMissCount() + firstCacheStats.getAndResetCachedMethodRequestCount());

                                cacheStats.setSecondCacheRequestCount(cacheStats.getSecondCacheRequestCount() + secondCacheStats.getAndResetCacheRequestCount());
                                cacheStats.setSecondCacheMissCount(cacheStats.getSecondCacheMissCount() + secondCacheStats.getAndResetCachedMethodRequestCount());

                                // 将缓存统计数据写到redis
                                redisTemplate.opsForValue().set(redisKey, cacheStats, 24, TimeUnit.HOURS);
                                logger.info(JSON.toJSONString(cacheStats));
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            }

            //  初始时间间隔是1分
        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 关闭线程池
     */
    public void shutdownExecutor() {
        executor.shutdown();
    }

    /**
     * 重置缓存统计数据
     */
    public void resetCacheStat() {
        RedisTemplate<String, Object> redisTemplate = cacheManager.getRedisTemplate();
        Set<String> cacheKeys = RedisHelper.scan(redisTemplate, CACHE_STATS_KEY_PREFIX + "*");

        for (String key : cacheKeys) {
            resetCacheStat(key);
        }
    }

    /**
     * 重置缓存统计数据
     *
     * @param redisKey redisKey
     */
    public void resetCacheStat(String redisKey) {
        RedisTemplate<String, Object> redisTemplate = cacheManager.getRedisTemplate();
        CacheStatsInfo cacheStats = (CacheStatsInfo) redisTemplate.opsForValue().get(redisKey);
        if (Objects.nonNull(cacheStats)) {
            cacheStats.clearStatsInfo();
            // 将缓存统计数据写到redis
            redisTemplate.opsForValue().set(redisKey, cacheStats, 24, TimeUnit.HOURS);
        }
    }

    public void setCacheManager(AbstractCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
