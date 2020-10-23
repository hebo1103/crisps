package net.crisps.cloud.framework.cache.aspectj.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 二级缓存配置项
 * @author Administrator
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface A {

    long expireTime() default 30;

    //建议是 expireTime * 0.2
    long preloadTime() default 6;

    TimeUnit timeUnit() default TimeUnit.MINUTES;

    boolean forceRefresh() default false;

    //是否允许存NULL
    boolean isAllowNullValue() default false;

    int magnification() default 1;
}
