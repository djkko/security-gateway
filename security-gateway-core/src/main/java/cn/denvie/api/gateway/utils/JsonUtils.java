package cn.denvie.api.gateway.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * JSON工具类。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class JsonUtils {

    // 默认的JSON解析器
    private static final ObjectMapper JSON_MAPPER = newObjectMapper();

    private static ObjectMapper newObjectMapper() {
        ObjectMapper result = new ObjectMapper();
        result.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        result.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // result.setSerializationInclusion(Include.NON_NULL);
        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        result.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        return result;
    }

    public static ObjectMapper getObjectMapper() {
        return JSON_MAPPER;
    }

    public static String writeValueAsString(Object value) {
        try {
            return value == null ? "" : JSON_MAPPER.writeValueAsString(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Map<String, Object> toMap(Object value) throws Exception {
        return convertValue(value, Map.class);
    }

    public static <T> T convertValue(Object value, Class<T> clazz) throws Exception {
        if (StringUtils.isEmpty(value)) return null;
        try {
            if (value instanceof String) {
                value = JSON_MAPPER.readTree((String) value);
            }
            return JSON_MAPPER.convertValue(value, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
