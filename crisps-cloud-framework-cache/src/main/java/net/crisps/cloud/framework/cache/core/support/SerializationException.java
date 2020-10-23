package net.crisps.cloud.framework.cache.core.support;

public class SerializationException extends NestedRuntimeException {

    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SerializationException(String msg) {
        super(msg);
    }
}
