package net.crisps.cloud.framework.cache.core.support;

public enum CacheMode {
    ONLY_FIRST("只是用一级缓存"),

    ONLY_SECOND("只是使用二级缓存"),

    ALL("同时开启一级缓存和二级缓存");

    private String label;

    CacheMode(String label) {
        this.label = label;
    }
}
