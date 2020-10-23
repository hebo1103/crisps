package net.crisps.cloud.framework.web.config;

import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

/**
 * @author Administrator
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(UtilRegistry.class)
public @interface EnableDggPortalUtil {
}
