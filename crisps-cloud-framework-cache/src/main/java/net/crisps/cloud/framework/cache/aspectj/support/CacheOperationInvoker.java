package net.crisps.cloud.framework.cache.aspectj.support;

/**
 * 抽象的调用缓存操作方法
 */
public interface CacheOperationInvoker {

    Object invoke() throws ThrowableWrapperException;

    class ThrowableWrapperException extends RuntimeException {

        private final Throwable original;

        public ThrowableWrapperException(Throwable original) {
            super(original.getMessage(), original);
            this.original = original;
        }

        public Throwable getOriginal() {
            return this.original;
        }
    }

}
