package net.crisps.cloud.framework.es.utils;

import com.github.pagehelper.PageInfo;
import java.util.List;

public class PageInfoUtils {
    public static <T> PageInfo<T> list2PageInfo(List<T> arrayList, Integer pageNum, Integer pageSize, Integer total) {
        PageInfo<T> pageInfo = new PageInfo<T>(arrayList);
        int pages = total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1;
        pageInfo.setPages(pages);
        pageInfo.setTotal(total);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setList(arrayList);
        boolean hasPreviousPage = pageNum == 1 ? false : true;
        pageInfo.setIsFirstPage(!hasPreviousPage);
        boolean isLastPage = (total  > pageSize * (pageNum - 1) && total <= pageSize * pageNum) ? true : false;
        pageInfo.setIsLastPage(isLastPage);
        return pageInfo;
    }
}
