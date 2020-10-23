package net.crisps.cloud.framework.cache.aspectj.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 删除缓存
 * @author Administrator
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DE {

    @AliasFor("cacheNames")
    String[] value() default {};

    @AliasFor("value")
    String[] cacheNames() default {};

    String key() default "";

    boolean ignoreException() default true;

    @Deprecated
    String keyGenerator() default "";

    boolean allEntries() default false;

}
