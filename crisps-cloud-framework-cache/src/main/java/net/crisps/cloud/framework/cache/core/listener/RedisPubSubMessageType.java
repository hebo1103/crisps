package net.crisps.cloud.framework.cache.core.listener;

public enum RedisPubSubMessageType {
    /**
     * 删除缓存
     */
    EVICT("删除缓存"),

    /**
     * 清空缓存
     */
    CLEAR("清空缓存"),


    /**
     * 清空一二级缓存
     */
    CLEAR_ALL("清空一二级缓存");

    private String label;

    RedisPubSubMessageType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
