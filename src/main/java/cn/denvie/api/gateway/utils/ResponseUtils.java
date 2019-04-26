package cn.denvie.api.gateway.utils;

import cn.denvie.api.gateway.common.ApiCode;
import cn.denvie.api.gateway.common.ApiResponse;

/**
 * 请求响应工具类。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class ResponseUtils {

    public static <T> ApiResponse<T> success(T data) {
        return build(ApiCode.SUCCESS, data);
    }

    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return build(code, message, data);
    }

    public static <T> ApiResponse<T> error(ApiCode errorCode, T data) {
        return build(errorCode, data);
    }

    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return build(code, message, data);
    }

    private static <T> ApiResponse<T> build(ApiCode apiCode, T data) {
        return build(apiCode.code(), apiCode.message(), data);
    }

    private static <T> ApiResponse<T> build(String code, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

}
