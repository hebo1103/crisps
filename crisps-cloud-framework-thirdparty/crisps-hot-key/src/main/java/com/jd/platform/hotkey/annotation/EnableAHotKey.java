package com.jd.platform.hotkey.annotation;

import com.jd.platform.hotkey.starter.Starter;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({Starter.class})
public @interface EnableAHotKey {
}

