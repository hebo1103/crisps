package net.crisps.cloud.framework.cache.aspectj.aspect;

import net.crisps.cloud.framework.cache.aspectj.annotation.A;
import net.crisps.cloud.framework.cache.aspectj.annotation.DC;
import net.crisps.cloud.framework.cache.aspectj.annotation.DE;
import net.crisps.cloud.framework.cache.aspectj.annotation.DP;
import net.crisps.cloud.framework.cache.aspectj.expression.CacheOperationExpressionEvaluator;
import net.crisps.cloud.framework.cache.aspectj.support.CacheOperationInvoker;
import net.crisps.cloud.framework.cache.aspectj.support.KeyGenerator;
import net.crisps.cloud.framework.cache.aspectj.support.SimpleKeyGenerator;
import net.crisps.cloud.framework.cache.core.cache.Cache;
import net.crisps.cloud.framework.cache.core.manager.CacheManager;
import net.crisps.cloud.framework.cache.core.setting.CacheSetting;
import net.crisps.cloud.framework.cache.core.setting.SecondaryCacheSetting;
import net.crisps.cloud.framework.cache.core.support.SerializationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

/**
 * 缓存拦截，用于注册方法信息
 *
 * @author Administrator
 */
@Aspect
public class CacheAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String CACHE_KEY_ERROR_MESSAGE = "缓存Key %s 不能为NULL";
    private static final String CACHE_NAME_ERROR_MESSAGE = "缓存名称不能为NULL";

    private final CacheOperationExpressionEvaluator evaluator = new CacheOperationExpressionEvaluator();

    @Autowired
    private CacheManager cacheManager;

    @Autowired(required = false)
    private KeyGenerator keyGenerator = new SimpleKeyGenerator();

    @Pointcut("@annotation(net.crisps.cloud.framework.cache.aspectj.annotation.DC)")
    public void cacheablePointcut() {
    }

    @Pointcut("@annotation(net.crisps.cloud.framework.cache.aspectj.annotation.DE)")
    public void cacheEvictPointcut() {
    }

    @Pointcut("@annotation(net.crisps.cloud.framework.cache.aspectj.annotation.DP)")
    public void cachePutPointcut() {
    }

    @Around("cacheablePointcut()")
    public Object cacheablePointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        CacheOperationInvoker aopAllianceInvoker = getCacheOperationInvoker(joinPoint);

        // 获取method
        Method method = this.getSpecificmethod(joinPoint);
        // 获取注解
        DC cacheable = AnnotationUtils.findAnnotation(method, DC.class);

        try {
            // 执行查询缓存方法
            return executeCacheable(aopAllianceInvoker, cacheable, method, joinPoint.getArgs(), joinPoint.getTarget());
        } catch (SerializationException e) {
            // 如果是序列化异常需要先删除原有缓存
            String[] cacheNames = cacheable.cacheNames();
            // 删除缓存
            delete(cacheNames, cacheable.key(), method, joinPoint.getArgs(), joinPoint.getTarget());

            // 忽略操作缓存过程中遇到的异常
            if (cacheable.ignoreException()) {
                logger.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        } catch (Exception e) {
            // 忽略操作缓存过程中遇到的异常
            if (cacheable.ignoreException()) {
                logger.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        }
    }


    @Around("cacheEvictPointcut()")
    public Object cacheEvictPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        CacheOperationInvoker aopAllianceInvoker = getCacheOperationInvoker(joinPoint);

        // 获取method
        Method method = this.getSpecificmethod(joinPoint);
        // 获取注解
        DE cacheEvict = AnnotationUtils.findAnnotation(method, DE.class);

        try {
            // 执行查询缓存方法
            return executeEvict(aopAllianceInvoker, cacheEvict, method, joinPoint.getArgs(), joinPoint.getTarget());
        } catch (Exception e) {
            // 忽略操作缓存过程中遇到的异常
            if (cacheEvict.ignoreException()) {
                logger.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        }
    }

    @Around("cachePutPointcut()")
    public Object cachePutPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        CacheOperationInvoker aopAllianceInvoker = getCacheOperationInvoker(joinPoint);

        // 获取method
        Method method = this.getSpecificmethod(joinPoint);
        // 获取注解
        DP cacheEvict = AnnotationUtils.findAnnotation(method, DP.class);

        try {
            // 执行查询缓存方法
            return executePut(aopAllianceInvoker, cacheEvict, method, joinPoint.getArgs(), joinPoint.getTarget());
        } catch (Exception e) {
            // 忽略操作缓存过程中遇到的异常
            if (cacheEvict.ignoreException()) {
                logger.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        }
    }

    /**
     * 执行Cacheable切面
     */
    private Object executeCacheable(CacheOperationInvoker invoker, DC cacheable,
                                    Method method, Object[] args, Object target) {

        // 解析SpEL表达式获取cacheName和key
        String[] cacheNames = cacheable.cacheNames();
        Assert.notEmpty(cacheable.cacheNames(), CACHE_NAME_ERROR_MESSAGE);
        String cacheName = cacheNames[0];
        Object key = generateKey(cacheable.key(), method, args, target);
        Assert.notNull(key, String.format(CACHE_KEY_ERROR_MESSAGE, cacheable.key()));

        // 从注解中获取缓存配置
        A secondaryCache = cacheable.cache();

        SecondaryCacheSetting secondaryCacheSetting = new SecondaryCacheSetting(secondaryCache.expireTime(),
                secondaryCache.preloadTime(), secondaryCache.timeUnit(), secondaryCache.forceRefresh(),
                secondaryCache.isAllowNullValue(), secondaryCache.magnification());

        CacheSetting cacheSetting = new CacheSetting(secondaryCacheSetting,
                cacheable.depict());

        // 通过cacheName和缓存配置获取Cache
        Cache cache = cacheManager.getCache(cacheName, cacheSetting);
        // 通Cache获取值
        Object o = cache.get(key, invoker::invoke);
        return o;
    }

    /**
     * 执行 CacheEvict 切面
     */
    private Object executeEvict(CacheOperationInvoker invoker, DE cacheEvict,
                                Method method, Object[] args, Object target) {
        // 解析SpEL表达式获取cacheName和key
        String[] cacheNames = cacheEvict.cacheNames();
        Assert.notEmpty(cacheEvict.cacheNames(), CACHE_NAME_ERROR_MESSAGE);
        // 判断是否删除所有缓存数据
        if (cacheEvict.allEntries()) {
            // 删除所有缓存数据（清空）
            for (String cacheName : cacheNames) {
                Collection<Cache> caches = cacheManager.getCache(cacheName);
                if (CollectionUtils.isEmpty(caches)) {
                    // 如果没有找到Cache就新建一个默认的
                    Cache cache = cacheManager.getCache(cacheName, new CacheSetting(new SecondaryCacheSetting(), "默认缓存配置（清除时生成）"));
                    cache.clear();
                } else {
                    for (Cache cache : caches) {
                        cache.clear();
                    }
                }
            }
        } else {
            // 删除指定key
            delete(cacheNames, cacheEvict.key(), method, args, target);
        }

        // 执行方法
        return invoker.invoke();
    }

    /**
     * 删除执行缓存名称上的指定key
     */
    private void delete(String[] cacheNames, String keySpEL, Method method, Object[] args, Object target) {
        Object key = generateKey(keySpEL, method, args, target);
        Assert.notNull(key, String.format(CACHE_KEY_ERROR_MESSAGE, keySpEL));
        for (String cacheName : cacheNames) {
            Collection<Cache> caches = cacheManager.getCache(cacheName);
            if (CollectionUtils.isEmpty(caches)) {
                // 如果没有找到Cache就新建一个默认的
                Cache cache = cacheManager.getCache(cacheName,
                        new CacheSetting(new SecondaryCacheSetting(), "默认缓存配置（删除时生成）"));
                cache.evict(key);
            } else {
                for (Cache cache : caches) {
                    cache.evict(key);
                }
            }
        }
    }

    /**
     * 执行 CachePut 切面
     */
    private Object executePut(CacheOperationInvoker invoker, DP cachePut, Method method, Object[] args, Object target) {


        String[] cacheNames = cachePut.cacheNames();
        Assert.notEmpty(cachePut.cacheNames(), CACHE_NAME_ERROR_MESSAGE);
        // 解析SpEL表达式获取 key
        Object key = generateKey(cachePut.key(), method, args, target);
        Assert.notNull(key, String.format(CACHE_KEY_ERROR_MESSAGE, cachePut.key()));

        // 从解决中获取缓存配置
        A secondaryCache = cachePut.cache();

        SecondaryCacheSetting secondaryCacheSetting = new SecondaryCacheSetting(secondaryCache.expireTime(),
                secondaryCache.preloadTime(), secondaryCache.timeUnit(), secondaryCache.forceRefresh(),
                secondaryCache.isAllowNullValue(), secondaryCache.magnification());

        CacheSetting cacheSetting = new CacheSetting(secondaryCacheSetting,
                cachePut.depict());

        // 指定调用方法获取缓存值
        Object result = invoker.invoke();

        for (String cacheName : cacheNames) {
            // 通过cacheName和缓存配置获取Cache
            Cache cache = cacheManager.getCache(cacheName, cacheSetting);
            cache.put(key, result);
        }

        return result;
    }

    private CacheOperationInvoker getCacheOperationInvoker(ProceedingJoinPoint joinPoint) {
        return () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable ex) {
                throw new CacheOperationInvoker.ThrowableWrapperException(ex);
            }
        };
    }

    /**
     * 解析SpEL表达式，获取注解上的key属性值
     */
    private Object generateKey(String keySpEl, Method method, Object[] args, Object target) {

        // 获取注解上的key属性值
        Class<?> targetClass = getTargetClass(target);
        if (StringUtils.hasText(keySpEl)) {
            EvaluationContext evaluationContext = evaluator.createEvaluationContext(method, args, target, targetClass, CacheOperationExpressionEvaluator.NO_RESULT);
            AnnotatedElementKey methodCacheKey = new AnnotatedElementKey(method, targetClass);
            // 兼容传null值得情况
            Object keyValue = evaluator.key(keySpEl, methodCacheKey, evaluationContext);
            return Objects.isNull(keyValue) ? "null" : keyValue;
        }
        return this.keyGenerator.generate(target, method, args);
    }

    /**
     * 获取类信息
     */
    private Class<?> getTargetClass(Object target) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }


    /**
     * 获取Method
     */
    private Method getSpecificmethod(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(pjp.getTarget());
        if (targetClass == null && pjp.getTarget() != null) {
            targetClass = pjp.getTarget().getClass();
        }
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        return specificMethod;
    }
}
