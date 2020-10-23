package net.crisps.cloud.framework.es.annotation;


import net.crisps.cloud.framework.es.enums.AnalyzerType;
import net.crisps.cloud.framework.es.enums.FieldType;

import java.lang.annotation.*;

/**
 * 作用在字段上，用于定义类型，映射关系
 * @author Administrator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface PortalEsField {

    FieldType type() default FieldType.TEXT;

    AnalyzerType analyzer() default AnalyzerType.IK_MAX_WORD;

}
