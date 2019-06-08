package cn.denvie.api.gateway.core;

import cn.denvie.api.gateway.common.*;
import cn.denvie.api.gateway.service.InvokeExceptionHandler;
import cn.denvie.api.gateway.service.ResponseService;
import cn.denvie.api.gateway.service.SignatureService;
import cn.denvie.api.gateway.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public static final boolean TIMESTAMP_CHECK_ENABLE = false;

    /**
     * 是否启用客户端设备校验
     */
    public static final boolean TIMESTAMP_DEVICE_ENABLE = true;

    /**
     * 允许的客户端请求时间与服务端时间差
     */
    public static final long TIMESTAMP_DIFFER = 10 * 60 * 1000;

    /**
     * Token的有效期（毫秒）
     */
    public static final long TOKEN_VAlID_TIME = 14 * 24 * 3600 * 1000;

    /**
     * 多设备登录策略
     */
    public static final MultiDeviceLogin MULTI_DEVICE_LOGIN = MultiDeviceLogin.REPLACE;

    /**
     * 默认的传参方式，默认为：BODY
     */
    public static final ParamType DEFAULT_PARAM_TYPE = ParamType.BODY;

    ///////////////////////////////////////////////////////////////////////////
    // Default Service Implement
    ///////////////////////////////////////////////////////////////////////////

    @Autowired
    ApiProperties apiProperties;

    /**
     * ResponseService的默认实现，调用方可自定义。
     */
    @Bean
    @ConditionalOnMissingBean(ResponseService.class)
    public ResponseService responseService() {

        return new ResponseService() {
            @Override
            public ApiResponse success(Object data) {
                return build(ApiCode.SUCCESS, data);
            }

            @Override
            public ApiResponse success(String code, String message, Object data) {
                return build(code, message, data);
            }

            @Override
            public ApiResponse error(String code, String message, Object data) {
                return build(code, message, data);
            }

            private ApiResponse build(ApiCode apiCode, Object data) {
                return build(apiCode.code(), apiCode.message(), data);
            }

            private ApiResponse build(String code, String message, Object data) {
                DefaultApiResponse<Object> response = new DefaultApiResponse<>();
                response.setCode(code);
                response.setMessage(message);
                response.setData(data);
                return response;
            }
        };
    }

    /**
     * SignatureService的默认实现，调用方可自定义。
     */
    @Bean
    @ConditionalOnMissingBean(SignatureService.class)
    public SignatureService signatureService() {

        return new SignatureService() {
            @Override
            public String sign(ApiRequest param) {
                String apiName = param.getApiName();
                String accessToken = param.getAccessToken();
                String secret = param.getSecret();
                String params = param.getParams();
                String timestamp = param.getTimestamp();
                StringBuilder keyBuilder = new StringBuilder();
                keyBuilder.append(secret)
                        .append(apiName)
                        .append(params)
                        .append(accessToken)
                        .append(timestamp)
                        .append(secret);
                return MD5Utils.md5(keyBuilder.toString()).toUpperCase();
            }
        };
    }

    /**
     * API调用异常处理的默认实现，调用方可自定义。
     */
    @Bean
    @ConditionalOnMissingBean(InvokeExceptionHandler.class)
    public InvokeExceptionHandler invokExceptionHandler(ResponseService responseService) {

        return new InvokeExceptionHandler() {
            @Override
            public ApiResponse handle(ApiRequest apiRequest, Throwable e) {
                String errMsg = e == null ?
                        ApiCode.FAILURE.message() : e.getMessage();
                return responseService.error(ApiCode.FAILURE.code(), errMsg, null);
            }
        };
    }

    /**
     * 默认的API接口请求拦截器。
     */
    @Bean
    @ConditionalOnMissingBean(ApiInvokeInterceptor.class)
    public ApiInvokeInterceptor apiInvokeInterceptor() {

        return new ApiInvokeInterceptor() {
            @Override
            public void before(ApiRequest request, Object[] args) {
            }

            @Override
            public void error(ApiRequest request, Throwable t) {
            }

            @Override
            public void after(ApiRequest request, Object result) {
            }
        };
    }

}
