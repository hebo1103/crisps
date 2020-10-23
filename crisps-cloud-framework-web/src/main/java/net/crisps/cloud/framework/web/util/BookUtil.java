//package net.crisps.cloud.framework.web.util;
//
//
//import lombok.extern.slf4j.Slf4j;
//import net.crisps.cloud.framework.web.domain.TreeBook;
//import net.crisps.cloud.framework.web.service.TreeBookService;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.List;
//
//
///**
// * @author Administrator
// */
//@Component
//@Slf4j
//public class BookUtil {
//
//    private static BookUtil bookUtil;
//
//    @Resource
//    private TreeBookService treeBookService;
//
//    @PostConstruct
//    public void init() {
//        log.info("*******初始化数据字典成功*******");
//        bookUtil = this;
//        bookUtil.treeBookService = this.treeBookService;
//    }
//
//    public static TreeBook queryBook(String code){
//        return bookUtil.treeBookService.selectTreeBookByCode(code);
//    }
//
//    public static List<TreeBook> queryBooks(List<String> list){
//        return bookUtil.treeBookService.selectTreeBookByCodes(list);
//    }
//
//}
