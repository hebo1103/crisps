package net.crisps.cloud.framework.cache.aspectj.support;

import java.lang.reflect.Method;

public interface KeyGenerator {

	Object generate(Object target, Method method, Object... params);

}
