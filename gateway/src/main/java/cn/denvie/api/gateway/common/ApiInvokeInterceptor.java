package cn.denvie.api.gateway.common;

import cn.denvie.api.gateway.core.ApiRequest;

/**
 * API接口请求拦截器。
 *
 * @author DengZhaoyong
 * @version 1.2.5
 */
public interface ApiInvokeInterceptor {

    void before(ApiRequest request, Object[] args);

    void error(ApiRequest request, Throwable t);

    void after(ApiRequest request, Object result);

}
