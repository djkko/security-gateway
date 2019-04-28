package cn.denvie.api.gateway.service;

import cn.denvie.api.gateway.common.ApiResponse;

/**
 * 请求响应服务。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public interface ResponseService {

    ApiResponse success(Object data);

    ApiResponse success(String code, String message, Object data);

    ApiResponse error(String code, String message, Object data);

}
