package cn.denvie.api.gateway.client;

import cn.denvie.api.gateway.common.ApiException;

/**
 * API安全网关客户端服务。
 *
 * @author DengZhaoyong
 * @version 1.3.0
 * @date 2019/6/19
 */
public interface ApiClientService {

    String post(InvokeParam.Builder paramBuilder) throws ApiException;

    <T> T post(InvokeParam.Builder paramBuilder, Class<T> clazz) throws ApiException;

}
