package net.crisps.cloud.framework.canal.annotation;

import net.crisps.cloud.framework.canal.config.CanalClientConfiguration;
import net.crisps.cloud.framework.canal.config.CanalConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * @author Administrator
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({CanalConfig.class, CanalClientConfiguration.class})
public @interface EnableDggPortalCanal {
}
