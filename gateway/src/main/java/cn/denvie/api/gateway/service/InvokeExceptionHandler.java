package cn.denvie.api.gateway.service;

import cn.denvie.api.gateway.common.ApiResponse;
import cn.denvie.api.gateway.core.ApiRequest;

/**
 * API调用异常处理器。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public interface InvokeExceptionHandler {
    /**
     * 处理API调用异常。
     *
     * @param apiRequest API请求参数
     * @param e          异常实例
     */
    ApiResponse handle(ApiRequest apiRequest, Throwable e);
}
