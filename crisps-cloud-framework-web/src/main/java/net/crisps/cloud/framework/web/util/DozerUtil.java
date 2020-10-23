package net.crisps.cloud.framework.web.util;


import com.github.dozermapper.core.Mapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author Administrator
 */
@Component
@Slf4j
public class DozerUtil {

    private static DozerUtil dozerUtil;

    @Resource
    private Mapper dozerMapper;

    public static <T,S> List<T> mapList(List<S> sList,Class<T> tClass){
        List<T> list = Lists.newArrayList();
        sList.forEach(item->{
            list.add(dozerUtil.dozerMapper.map(item,tClass));
        });
        return list;
    }

    @PostConstruct
    public void init() {
        log.debug("*******初始化Dozer成功*******");
        dozerUtil = this;
        dozerUtil.dozerMapper = this.dozerMapper;
    }

    public static <T> T map(Object obj,Class<T> tClass){
        return dozerUtil.dozerMapper.map(obj,tClass);
    }

    public static Mapper getDozerMapper(){
        return dozerUtil.dozerMapper;
    }

}
