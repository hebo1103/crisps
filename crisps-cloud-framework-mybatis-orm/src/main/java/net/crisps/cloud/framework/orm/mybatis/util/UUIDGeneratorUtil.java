package net.crisps.cloud.framework.orm.mybatis.util;

import java.util.UUID;

/**
 * @author Administrator
 */
public class UUIDGeneratorUtil {

    public static String generate() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
