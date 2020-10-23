package net.crisps.cloud.framework.canal.annotation;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author Administrator
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ListenPoint(eventType = CanalEntry.EventType.INSERT)
public @interface InsertListenPoint {

    @AliasFor(annotation = ListenPoint.class)
    String destination() default "";

    @AliasFor(annotation = ListenPoint.class)
    String[] schema() default {};

    @AliasFor(annotation = ListenPoint.class)
    String[] table() default {};

}
