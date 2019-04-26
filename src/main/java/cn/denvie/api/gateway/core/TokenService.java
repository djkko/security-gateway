package cn.denvie.api.gateway.core;

import cn.denvie.api.gateway.common.TokenParam;

/**
 * Token服务。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public interface TokenService {

    ApiToken createToken(TokenParam param);

    ApiToken getToken(String token);

}
