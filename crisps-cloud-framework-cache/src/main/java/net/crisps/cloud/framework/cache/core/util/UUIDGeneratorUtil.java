package net.crisps.cloud.framework.cache.core.util;

import java.util.UUID;

public class UUIDGeneratorUtil {

    public static String generate() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
