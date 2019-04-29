package cn.denvie.api.gateway.service;

import cn.denvie.api.gateway.core.ApiRequest;

/**
 * 签名服务，定义签名生成的规则及实现。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public interface SignatureService {

    /**
     * 执行签名。
     *
     * @param param 请求参数
     * @return 经过签名后的字符串
     */
    String sign(ApiRequest param);

}
