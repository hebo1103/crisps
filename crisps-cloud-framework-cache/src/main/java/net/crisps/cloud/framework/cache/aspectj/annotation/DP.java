package net.crisps.cloud.framework.cache.aspectj.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 将对应数据放到缓存中
 * @author Administrator
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DP {

    @AliasFor("cacheNames")
    String[] value() default {};

    /**
     * 缓存名称
     */
    @AliasFor("value")
    String[] cacheNames() default {};

    String depict() default "";

    String key() default "";

    @Deprecated
    String keyGenerator() default "";

    boolean ignoreException() default true;

    /**
     * 二级缓存配置
     */
    A cache() default @A();
}
