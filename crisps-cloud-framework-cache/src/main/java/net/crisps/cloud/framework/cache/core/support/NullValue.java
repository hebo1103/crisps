
package net.crisps.cloud.framework.cache.core.support;

import java.io.Serializable;

public final class NullValue implements Serializable {

    public static final Object INSTANCE = new NullValue();

    private static final long serialVersionUID = 1L;

    private Object readResolve() {
        return INSTANCE;
    }
}
