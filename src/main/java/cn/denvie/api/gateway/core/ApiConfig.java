package cn.denvie.api.gateway.core;

import cn.denvie.api.gateway.common.*;
import cn.denvie.api.gateway.service.ResponseService;
import cn.denvie.api.gateway.service.TokenService;
import cn.denvie.api.gateway.utils.RSAUtils;
import cn.denvie.api.gateway.utils.RandomUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Api配置。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ApiProperties.class)
public class ApiConfig {

    /**
     * 加密方式
     */
    public static final EnctyptType ENCTYPT_TYPE = EnctyptType.AES;

    /**
     * 是否启用客户端与服务端时间差校验
     */
    public static final boolean TIMESTAMP_ENABLE = true;

    /**
     * 允许的客户端请求时间与服务端时间差
     */
    public static final long TIMESTAMP_DIFFER = 10 * 60 * 1000;

    /**
     * Token的有效期（毫秒）
     */
    public static final long EXPIRE_TIME = 7 * 24 * 3600 * 1000;

    ///////////////////////////////////////////////////////////////////////////
    // Default Service Implement
    ///////////////////////////////////////////////////////////////////////////

    /**
     * TokenService的默认实现，调用方可自定义。
     */
    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    public TokenService tokenService() {

        return new TokenService() {

            private ConcurrentHashMap<String, ApiToken> sTokenMap = new ConcurrentHashMap<>();
            private ConcurrentHashMap<String, String> sUserTokenMap = new ConcurrentHashMap<>();

            @Override
            public ApiToken createToken(TokenParam param) {
                if (param == null) return null;

                String token = sUserTokenMap.get(param.getMemberId());
                ApiToken apiToken = StringUtils.isEmpty(token) ? null : sTokenMap.get(token);
                if (apiToken == null) {
                    apiToken = new ApiToken();
                    apiToken.setId(System.currentTimeMillis());
                    apiToken.setMemberId(param.getMemberId());
                    apiToken.setAccessToken(RandomUtils.generateUuid());

                    sTokenMap.put(apiToken.getAccessToken(), apiToken);
                    sUserTokenMap.put(param.getMemberId(), apiToken.getAccessToken());
                }

                // 生成密钥
                if (ApiConfig.ENCTYPT_TYPE == EnctyptType.AES) {
                    apiToken.setSecret(RandomUtils.generateSecret());
                } else if (ApiConfig.ENCTYPT_TYPE == EnctyptType.RSA) {
                    Map<String, String> keyMap = RSAUtils.generateRSAKeyBase64(512);
                    String privateKey = keyMap.get(RSAUtils.KEY_PRIVATE);
                    String publicKey = keyMap.get(RSAUtils.KEY_PUBLIC);
                    apiToken.setSecret(publicKey);
                    apiToken.setPrivateScret(privateKey);
                }

                apiToken.setClientIp(param.getClientIp());
                apiToken.setClientType(param.getClientType());
                apiToken.setClientCode(param.getClientCode());
                apiToken.setClientUserCode(param.getClientUserCode());
                apiToken.setCreateTime(System.currentTimeMillis());
                apiToken.setExpireTime(apiToken.getCreateTime() + ApiConfig.EXPIRE_TIME);

                return apiToken;
            }

            @Override
            public ApiToken getToken(String token) {
                return sTokenMap.get(token);
            }
        };
    }

    /**
     * ResponseService的默认实现，调用方可自定义。
     */
    @Bean
    @ConditionalOnMissingBean(ResponseService.class)
    public ResponseService responseService() {

        return new ResponseService() {
            @Override
            public <T> ApiResponse<T> success(T data) {
                return build(ApiCode.SUCCESS, data);
            }

            @Override
            public <T> ApiResponse<T> success(String code, String message, T data) {
                return build(code, message, data);
            }

            @Override
            public <T> ApiResponse<T> error(String code, String message, T data) {
                return build(code, message, data);
            }

            private <T> ApiResponse<T> build(ApiCode apiCode, T data) {
                return build(apiCode.code(), apiCode.message(), data);
            }

            private <T> ApiResponse<T> build(String code, String message, T data) {
                ApiResponse<T> response = new ApiResponse<>();
                response.setCode(code);
                response.setMessage(message);
                response.setData(data);
                return response;
            }
        };
    }

}
