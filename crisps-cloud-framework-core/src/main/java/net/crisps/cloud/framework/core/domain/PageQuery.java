package net.crisps.cloud.framework.core.domain;

import com.google.common.base.Splitter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApiModel(value = "分页参数数据模型")
public class PageQuery implements Serializable {
    private static final long serialVersionUID = -1282316724847196313L;
    @ApiModelProperty(value = "页码，默认1")
    private int page = 1;
    @ApiModelProperty(value = "长度，默认10")
    private int size = 10;
    @ApiModelProperty(value = "过滤条件，key=value;key1=value1")
    private String filter;
    @ApiModelProperty(value = "排序条件")
    private String order;
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Map<String, String> convertFilterToMap() {
        if (StringUtils.isEmpty(filter)) {
            return new HashMap<>();
        }
        List<String> list = Splitter.on(";").omitEmptyStrings().trimResults()
                .splitToList(filter);
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        Map<String, String> map = new HashMap<String, String>();
        for (String s : list) {
            List<String> item = Splitter.on("=").limit(2).omitEmptyStrings().trimResults()
                    .splitToList(s);
            if (item.size() == 2) {
                map.put(item.get(0), item.get(1));
            }
        }
        return map;
    }

    public String convertSort() {
        if (StringUtils.isEmpty(order)) {
            return "";
        }
        List<String> list = Splitter.on(",").omitEmptyStrings().trimResults()
                .splitToList(order);

        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            List<String> item = Splitter.on("=").limit(2).omitEmptyStrings().trimResults()
                    .splitToList(s);
            if (item.size() < 2 || item.size() > 2) {
                continue;
            }
            String fieldName = item.get(0);
            String sortDirection = item.get(1);
            sb.append(fieldName);
            sb.append(" ");
            sb.append(sortDirection);
            sb.append(",");
        }
        if (StringUtils.isEmpty(sb.toString())) {
            return "";
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    public String cacheKey(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getFilter());
        stringBuilder.append(getOrder());
        stringBuilder.append(getPage());
        stringBuilder.append(getSize());
       return UUID.nameUUIDFromBytes(stringBuilder.toString().getBytes()).toString().replace("-","");
    }
}
