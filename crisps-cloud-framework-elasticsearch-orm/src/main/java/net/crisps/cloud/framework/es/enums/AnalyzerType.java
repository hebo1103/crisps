package net.crisps.cloud.framework.es.enums;

import lombok.Getter;

/**
 * @author Administrator
 */

@Getter
public enum AnalyzerType{
    /**
     * 不使用分词器
     */
    NO("不使用分词"),
    /**
     * 标准分词，默认分词器
     */
    STANDARD("standard"),
    /**
     * IK
     */
    IK_SMART("ik_smart"),
    /**
     * ik_max_word
     */
    IK_MAX_WORD("ik_max_word");

    private String type;

    AnalyzerType(String type){
        this.type = type;
    }


}
