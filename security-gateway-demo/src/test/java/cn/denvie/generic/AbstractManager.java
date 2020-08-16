package cn.denvie.generic;

import java.util.List;

public class AbstractManager {

    protected <T> void invoke(String data, Callback<Result<T>> callback, Class<T> clazz) {
        Result<T> result = GsonUtils.fromJsonObject(data, clazz);
        callback.onDataLoaded(result);
    }

    protected <T> void invokeList(String data, Callback<Result<List<T>>> callback, Class<T> clazz) {
        Result<List<T>> result = GsonUtils.fromJsonArray(data, clazz);
        callback.onDataLoaded(result);
    }

}
