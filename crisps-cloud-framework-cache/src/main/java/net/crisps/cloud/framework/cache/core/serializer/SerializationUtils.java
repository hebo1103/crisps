package net.crisps.cloud.framework.cache.core.serializer;

/**
 * @author Administrator
 */
public abstract class SerializationUtils {

    static final byte[] EMPTY_ARRAY = new byte[0];

    static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }
}
