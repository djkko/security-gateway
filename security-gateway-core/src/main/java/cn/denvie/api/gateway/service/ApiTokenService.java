package cn.denvie.api.gateway.service;

import cn.denvie.api.gateway.common.ApiException;
import cn.denvie.api.gateway.common.TokenParam;
import cn.denvie.api.gateway.core.ApiToken;

/**
 * Token服务。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public interface ApiTokenService {

    /**
     * 生成Token并持久化存储。
     *
     * @param param 参数
     * @return ApiToken
     */
    ApiToken createToken(TokenParam param) throws ApiException;

    /**
     * 根据token获取对应的ApiToken实例。
     *
     * @param token token值
     * @return
     */
    ApiToken getToken(String token);

    /**
     * 更新指定Token失效时间
     *
     * @param token Token
     */
    void updateExpireTimeByToken(String token);

}
