//package net.crisps.cloud.framework.es.builder;
//
//import net.crisps.cloud.framework.es.enums.FieldType;
//import net.crisps.cloud.framework.es.annotation.PortalEsField;
//import org.elasticsearch.common.xcontent.XContentBuilder;
//import org.elasticsearch.common.xcontent.XContentFactory;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//
///**
// * @author Administrator
// */
//public class MappingBuilder {
//    public static XContentBuilder generateBuilder(Class clazz) throws IOException {
//        XContentBuilder builder = XContentFactory.jsonBuilder();
//        builder.startObject();
//        builder.startObject("properties");
//        Field[] declaredFields = clazz.getDeclaredFields();
//        for (Field f : declaredFields) {
//            if (f.isAnnotationPresent(PortalEsField.class)) {
//                PortalEsField declaredAnnotation = f.getDeclaredAnnotation(PortalEsField.class);
//                if (declaredAnnotation.type() == FieldType.OBJECT) {
//                    Class<?> type = f.getType();
//                    Field[] df2 = type.getDeclaredFields();
//                    builder.startObject(f.getName());
//                    builder.startObject("properties");
//                    for (Field f2 : df2) {
//                        if (f2.isAnnotationPresent(PortalEsField.class)) {
//                            PortalEsField declaredAnnotation2 = f2.getDeclaredAnnotation(PortalEsField.class);
//                            builder.startObject(f2.getName());
//                            builder.field("type", declaredAnnotation2.type().getType());
//                            // keyword不需要分词
//                            if (declaredAnnotation2.type() == FieldType.TEXT) {
//                                builder.field("analyzer", declaredAnnotation2.analyzer().getType());
//                            }
//                            builder.endObject();
//                        }
//                    }
//                    builder.endObject();
//                    builder.endObject();
//                }else{
//                    builder.startObject(f.getName());
//                    builder.field("type", declaredAnnotation.type().getType());
//                    // keyword不需要分词
//                    if (declaredAnnotation.type() == FieldType.TEXT) {
//                        builder.field("analyzer", declaredAnnotation.analyzer().getType());
//                    }
//                    builder.endObject();
//                }
//            }
//        }
//        // 对应property
//        builder.endObject();
//        builder.endObject();
//        return builder;
//    }
//}
