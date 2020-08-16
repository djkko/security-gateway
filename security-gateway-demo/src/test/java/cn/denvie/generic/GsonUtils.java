package cn.denvie.generic;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class GsonUtils {

    // 处理 data 为 object 的情况
    public static <T> Result<T> fromJsonObject(String data, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(Result.class, new Class[]{clazz});
        return new Gson().fromJson(data, type);
    }

    // 处理 data 为 array 的情况
    public static <T> Result<List<T>> fromJsonArray(String data, Class<T> clazz) {
        // 生成Result 中的 List<T>
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        // 根据List<T>生成完整的Result<List<T>>
        Type type = new ParameterizedTypeImpl(Result.class, new Type[]{listType});
        return new Gson().fromJson(data, type);
    }

}
