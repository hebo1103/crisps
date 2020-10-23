package net.crisps.cloud.framework.orm.mybatis.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Administrator
 */
public class ParameterUtil {

    /**
     * 解析查询条件 生成map
     * @param pageQuery  name=name;age=2;type=1,2 格式
     * @return
     */
    public static Map stringToMap(String pageQuery){
        Map map =  new HashMap();
        if(null != pageQuery && !pageQuery.equals("")){
            String[] params = pageQuery.split(";");
            for(String str : params){
                String[] keyValue = str.split("=",0);
                if(keyValue.length == 2) {
                    if(!keyValue[1].equals("undefined")) {
                        if (keyValue[1].indexOf(",") != -1) {
                            map.put(keyValue[0], keyValue[1].split(","));
                        } else {
                            map.put(keyValue[0], keyValue[1]);
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * 组装查询条件 生成Sqting
     * @param map
     * @return
     */
    public static String mapToString(Map map){
        StringBuffer pageQuery = new StringBuffer();
        Set keySet = map.keySet();
        Iterator itr = keySet.iterator();
        while (itr.hasNext()){
            String key = (String)itr.next();
            Object value = map.get(key);
            if(value.getClass().isArray()){
                pageQuery.append(key).append("=");
                for(Object val : (Object[])value){
                    pageQuery.append(val).append(",");
                }
                pageQuery.deleteCharAt(pageQuery.length()-1);
                pageQuery.append(";");
            }else {
                pageQuery.append(key).append("=").append(value).append(";");
            }
        }
        return pageQuery.toString();
    }
}
