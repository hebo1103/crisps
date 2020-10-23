package net.crisps.cloud.framework.cache.core.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.crisps.cloud.framework.cache.core.support.SerializationException;
import net.crisps.cloud.framework.cache.core.support.NullValue;
import net.crisps.cloud.framework.cache.core.support.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.charset.Charset;

public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
    private Logger logger = LoggerFactory.getLogger(FastJsonRedisSerializer.class);

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Class<T> clazz;

    /**
     * 允许所有包的序列化和反序列化，不推荐
     */
    @Deprecated
    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
        try {
            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        } catch (Throwable e) {
            logger.warn("版本太低", e);
        }
    }

    public FastJsonRedisSerializer(Class<T> clazz, String... packages) {
        super();
        this.clazz = clazz;
        try {
            ParserConfig.getGlobalInstance().addAccept("com.github.xiaolyuh.");
            if (packages != null && packages.length > 0) {
                for (String packageName : packages) {
                    ParserConfig.getGlobalInstance().addAccept(packageName);
                }
            }
        } catch (Throwable e) {
            logger.warn("版本太低", e);
        }
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        try {
            return JSON.toJSONString(new FastJsonSerializerWrapper(t), SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new SerializationException(String.format("FastJsonRedisSerializer 序列化异常: %s, 【JSON：%s】",
                    e.getMessage(), JSON.toJSONString(t)), e);

        }

    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (SerializationUtils.isEmpty(bytes)) {
            return null;
        }

        String str = new String(bytes, DEFAULT_CHARSET);
        try {
            FastJsonSerializerWrapper wrapper = JSON.parseObject(str, FastJsonSerializerWrapper.class);
            switch (Type.parse(wrapper.getType())) {
                case STRING:
                    return (T) wrapper.getContent();
                case OBJECT:
                case SET:

                    if (wrapper.getContent() instanceof NullValue) {
                        return null;
                    }

                    return (T) wrapper.getContent();

                case LIST:

                    return (T) ((JSONArray) wrapper.getContent()).toJavaList(clazz);

                case NULL:

                    return null;
                default:
                    throw new SerializationException("不支持反序列化的对象类型: " + wrapper.getType());
            }
        } catch (Exception e) {
            throw new SerializationException(String.format("FastJsonRedisSerializer 反序列化异常: %s, 【JSON：%s】",
                    e.getMessage(), str), e);
        }
    }
}
