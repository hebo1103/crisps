package net.crisps.cloud.framework.cache.aspectj.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author Administrator
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DC {

    @AliasFor("cacheNames")
    String[] value() default {};

    @AliasFor("value")
    String[] cacheNames() default {};

    String depict() default "";

    String key() default "";

    @Deprecated
    String keyGenerator() default "";

    boolean ignoreException() default true;

    A cache() default @A();
}
