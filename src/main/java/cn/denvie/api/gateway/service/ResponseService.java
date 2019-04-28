package cn.denvie.api.gateway.service;

import cn.denvie.api.gateway.common.ApiResponse;

/**
 * 请求响应服务。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public interface ResponseService {

    <T> ApiResponse<T> success(T data);

    <T> ApiResponse<T> success(String code, String message, T data);

    <T> ApiResponse<T> error(String code, String message, T data);

}
