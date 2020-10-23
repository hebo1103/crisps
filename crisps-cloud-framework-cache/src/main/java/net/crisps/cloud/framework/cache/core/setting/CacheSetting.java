package net.crisps.cloud.framework.cache.core.setting;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class CacheSetting implements Serializable {
    private static final String SPLIT = "-";
    private String internalKey;

    private String depict;

    boolean useFirstCache = false;


    private SecondaryCacheSetting secondaryCacheSetting;

    public CacheSetting() {
    }

    public CacheSetting(SecondaryCacheSetting secondaryCacheSetting,
                        String depict) {
        this.secondaryCacheSetting = secondaryCacheSetting;
        this.depict = depict;
        internalKey();
    }

    @JSONField(serialize = false, deserialize = false)
    private void internalKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(SPLIT);
        if (secondaryCacheSetting != null) {
            sb.append(secondaryCacheSetting.getTimeUnit().toMillis(secondaryCacheSetting.getExpiration()));
            sb.append(SPLIT);
            sb.append(secondaryCacheSetting.getTimeUnit().toMillis(secondaryCacheSetting.getPreloadTime()));
        }
        internalKey = sb.toString();
    }

    public SecondaryCacheSetting getSecondaryCacheSetting() {
        return secondaryCacheSetting;
    }

    public String getInternalKey() {
        return internalKey;
    }


    public String getDepict() {
        return depict;
    }

}
