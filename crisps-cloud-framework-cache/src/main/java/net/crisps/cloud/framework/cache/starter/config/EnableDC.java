package net.crisps.cloud.framework.cache.starter.config;

import com.jd.platform.hotkey.annotation.EnableAHotKey;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CacheAutoConfig.class})
@EnableAHotKey
public @interface EnableDC {
}
