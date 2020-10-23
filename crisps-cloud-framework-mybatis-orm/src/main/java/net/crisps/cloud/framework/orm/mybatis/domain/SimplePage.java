package net.crisps.cloud.framework.orm.mybatis.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 分页数据模型
 * @author Administrator
 */
@ApiModel(description = "分页数据模型")
public class SimplePage<T> implements Serializable {

    private static final long serialVersionUID = -547498499589858560L;

    /**
     * 总页数
     */
    @ApiModelProperty(value = "总页数",example = "10")
    private int totalPages;

    /**
     * 总记录数
     */
    @ApiModelProperty(value = "总记录数",example = "100")
    @JsonProperty("iTotalRecords")
    private long totalElements;

    /**
     * 当前页数
     */
    @ApiModelProperty(value = "当前页数",example = "1")
    private int number;

    /**
     * 每页记录条数
     */
    @ApiModelProperty(value = "每页记录条数",example = "20")
    private int size = 20;

    /**
     * 数据集合
     */
    @JsonProperty("aaData")
    @ApiModelProperty(value = "数据集合")
    private List<T> content = new ArrayList<T>();

    /**
     * 是否存在数据
     */
    @ApiModelProperty(value = "是否存在数据")
    private boolean hasContent = false;

    /**
     * 是否为第一页
     */
    @ApiModelProperty(value = "是否为第一页")
    private boolean isFirst = true;

    /**
     * 是否为最后一页
     */
    @ApiModelProperty(value = "是否为最后一页")
    private boolean isLast = true;

    /**
     * 总页数
     *
     * @return 总页数
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * 总页数
     *
     * @param totalPages 总页数
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * 总记录数
     *
     * @return 总记录数
     */
    public long getTotalElements() {
        return totalElements;
    }

    /**
     * 总记录数
     *
     * @param totalElements 总记录数
     */
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    /**
     * 当前页数
     *
     * @return 当前页数
     */
    public int getNumber() {
        return number;
    }

    /**
     * 当前页数
     *
     * @param number 当前页数
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * 每页记录条数
     *
     * @return 每页记录条数
     */
    public int getSize() {
        return size;
    }

    /**
     * 每页记录条数
     *
     * @param size 每页记录条数
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * 数据集合
     *
     * @return 数据集合
     */
    public List<T> getContent() {
        return content;
    }

    /**
     * 数据集合
     *
     * @param content 数据集合
     */
    public void setContent(List<T> content) {
        this.content = content;
    }

    /**
     * 是否存在数据
     *
     * @return 是否存在数据
     */
    public boolean isHasContent() {
        return hasContent;
    }

    /**
     * 是否存在数据
     *
     * @param hasContent 是否存在数据
     */
    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }

    /**
     * 是否为第一页
     *
     * @return 是否为第一页
     */
    public boolean isFirst() {
        return isFirst;
    }

    /**
     * 是否为第一页
     *
     * @param first 是否为第一页
     */
    public void setFirst(boolean first) {
        isFirst = first;
    }

    /**
     * 是否为最后一页
     *
     * @return 是否为最后一页
     */
    public boolean isLast() {
        return isLast;
    }

    /**
     * 是否为最后一页
     *
     * @param last 是否为最后一页
     */
    public void setLast(boolean last) {
        isLast = last;
    }

    public SimplePage<T> convert(PageInfo<T> page) {
        SimplePage<T> simplePage = new SimplePage<T>();
        if (page != null) {
            simplePage.setTotalPages(page.getPages());
            simplePage.setTotalElements(page.getTotal());
            simplePage.setNumber(page.getPageNum());
            simplePage.setSize(page.getSize());
            simplePage.setContent(page.getList());
            simplePage.setHasContent(CollectionUtils.isNotEmpty(page.getList()));
            simplePage.setFirst(page.isIsFirstPage());
            simplePage.setLast(page.isIsLastPage());
        }
        return simplePage;
    }

    public static <T> SimplePage<T> of(PageInfo<T> list) {
        return new SimplePage<T>().convert(list);
    }

}
