package net.crisps.cloud.framework.es.enums;

import lombok.Getter;

/**
 * @author Administrator
 */
@Getter
public enum FieldType {
    /**
     * text
     */
    TEXT("text"),

    KEYWORD("keyword"),

    INTEGER("integer"),

    DOUBLE("double"),

    DATE("date"),

    LONG("long"),

    /**
     * 单条数据
     */
    OBJECT("object"),

    /**
     * 嵌套数组
     */
    NESTED("nested");


    FieldType(String type){
        this.type = type;
    }

    private String type;

}
