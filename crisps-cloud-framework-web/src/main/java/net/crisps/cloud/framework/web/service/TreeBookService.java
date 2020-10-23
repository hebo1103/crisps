//package net.crisps.cloud.framework.web.service;
//
//import com.google.common.collect.Lists;
//import net.dgg.framework.tac.elasticsearch.DggESTemplate;
//import net.crisps.cloud.framework.web.domain.TreeBook;
//import org.apache.commons.collections.CollectionUtils;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.stereotype.Service;
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * @author Alan
// * @date 2020/7/17 0017 13:30
// */
//@Service
//public class TreeBookService{
//
//    @Resource
//    private DggESTemplate dggESTemplate;
//
//    public TreeBook selectTreeBookByCode(String code) {
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        queryBuilder.must(QueryBuilders.matchQuery("code",code));
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//        builder.query(queryBuilder);
//        List<TreeBook> treeBooks = dggESTemplate.retrieveDocument(builder, TreeBook.class);
//        if(CollectionUtils.isEmpty(treeBooks)){
//            return null;
//        }
//        return treeBooks.get(0);
//    }
//
//    public List<TreeBook> selectTreeBookByCodes(List<String> pcodes){
//        if(CollectionUtils.isEmpty(pcodes)){
//            return Lists.newArrayList();
//        }
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        BoolQueryBuilder pCodesQuery = QueryBuilders.boolQuery();
//        pcodes.forEach(x->{
//            pCodesQuery.should(QueryBuilders.matchPhraseQuery("pcode", x));
//        });
//        boolQueryBuilder.must(pCodesQuery);
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//        builder.query(boolQueryBuilder);
//        builder.size(2000);
//        List<TreeBook> treeBooks = dggESTemplate.retrieveDocument(builder, TreeBook.class);
//        return treeBooks;
//    }
//
//}
