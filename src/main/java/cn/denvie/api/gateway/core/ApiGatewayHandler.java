package cn.denvie.api.gateway.core;

import cn.denvie.api.gateway.common.*;
import cn.denvie.api.gateway.service.ResponseService;
import cn.denvie.api.gateway.service.SignatureService;
import cn.denvie.api.gateway.service.TokenService;
import cn.denvie.api.gateway.utils.AESUtils;
import cn.denvie.api.gateway.utils.JsonUtils;
import cn.denvie.api.gateway.utils.RSAUtils;
import cn.denvie.api.gateway.service.InvokExceptionHandler;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * API请求处理器。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
@Component
public class ApiGatewayHandler implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayHandler.class);

    @Autowired
    private TokenService tokenService;
    @Autowired
    private ResponseService responseService;
    @Autowired
    private SignatureService signatureService;
    @Autowired
    ApiProperties apiProperties;
    @Autowired
    InvokExceptionHandler invokExceptionHandler;

    private ParameterNameDiscoverer parameterUtils;
    private ApiRegisterCenter apiRegisterCenter;

    public ApiGatewayHandler() {
        parameterUtils = new LocalVariableTableParameterNameDiscoverer();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        apiRegisterCenter = new ApiRegisterCenter(context);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        apiRegisterCenter.loadApiFromSpringBeans();
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) {
        String apiName = request.getParameter(ApiParam.API_NAME);
        String apiParams = request.getParameter(ApiParam.API_PARAMS);
        ApiResponse apiResponse;
        ApiRegisterCenter.ApiRunnable apiRunnable;
        ApiRequest apiRequest = null;
        ApiToken apiToken = null;
        try {
            // 系统参数验证
            apiRunnable = valdateSysParams(request);
            // 构建ApiRequest
            apiRequest = buildApiRequest(request);
            // 验证Token
            if (!StringUtils.isEmpty(apiRequest.getAccessToken())) {
                apiToken = checkToken(apiRequest, apiRunnable.getApiMapping().needLogin());
            }
            // 验证签名和参数
            checkSignAndParams(apiRequest, apiToken);
            // 登录验证
            if (apiRunnable.getApiMapping().needLogin()) {
                if (!apiRequest.isLogin()) {
                    throw new ApiException(ApiCode.UN_LOGIN);
                }
            }

            Object[] args = buildParams(apiRunnable, apiRequest.getParams(), request, response, apiRequest);
            logger.info("请求接口【" + apiName + "】, 参数=" + apiRequest.getParams());
            apiResponse = responseService.success(apiRunnable.run(args));
        } catch (ApiException e) {
            if (apiRequest != null) {
                apiParams = apiRequest.getParams();
            }
            logger.error("调用接口【" + apiName + "】异常，" + e.getMessage() + "，参数=" + apiParams/*, e*/);
            apiResponse = responseService.error(e.getCode(), e.getMessage(), null);
        } catch (InvocationTargetException e) {
            if (apiRequest != null) {
                apiParams = apiRequest.getParams();
            }
            String errMsg = e.getTargetException() == null ?
                    e.toString() : e.getTargetException().getMessage();
            logger.error("调用接口【" + apiName + "】异常，" + errMsg + "，参数=" + apiParams/*, e.getTargetException()*/);
            apiResponse = invokExceptionHandler.handle(apiRequest, e.getTargetException());
        } catch (Exception e) {
            logger.error("其他异常", e);
            ApiException apiException = new ApiException(e.getMessage());
            apiResponse = responseService.error(apiException.getCode(), apiException.getMessage(), null);
        }

        // 统一返回结果
        returnResult(apiResponse, response);
    }

    // 根据HttpServletRequest构建Api请求参数
    private ApiRequest buildApiRequest(HttpServletRequest request) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setApiName(request.getParameter(ApiParam.API_NAME));

        // params参数处理
        String params = request.getParameter(ApiParam.API_PARAMS);
        if (params == null) {
            params = "";
        }
        apiRequest.setParams(params);

        // Token支持Header和Param方式传值
        apiRequest.setAccessToken(request.getHeader(ApiParam.API_TOKEN));
        if (StringUtils.isEmpty(apiRequest.getAccessToken())) {
            apiRequest.setAccessToken(request.getParameter(ApiParam.API_TOKEN));
        }

        // 设备类型及设备唯一标识支持Header和Param方式传值
        apiRequest.setClientType(request.getHeader(ApiParam.API_CLIENT_TYPE));
        if (StringUtils.isEmpty(apiRequest.getClientType())) {
            apiRequest.setClientType(request.getParameter(ApiParam.API_CLIENT_TYPE));
        }
        apiRequest.setClientCode(request.getHeader(ApiParam.API_CLIENT_CODE));
        if (StringUtils.isEmpty(apiRequest.getClientCode())) {
            apiRequest.setClientCode(request.getParameter(ApiParam.API_CLIENT_CODE));
        }

        apiRequest.setTimestamp(request.getParameter(ApiParam.API_TIMESTAMP));
        apiRequest.setSign(request.getParameter(ApiParam.API_SIGN));

        return apiRequest;
    }

    // 验证Token
    private ApiToken checkToken(ApiRequest request, boolean needLogin) throws ApiException {
        // 验证Token
        ApiToken token = tokenService.getToken(request.getAccessToken());
        if (token == null) {
            if (needLogin) {
                throw new ApiException(ApiCode.CHECK_TOKEN_NULL);
            }
            return null;
        }
        if (new Date(token.getExpireTime()).before(new Date())) {
            if (needLogin) {
                throw new ApiException(ApiCode.CHECK_TOKEN_INVALID);
            }
            return null;
        }

        // 注入用户信息
        request.setMemberId(token.getMemberId());
        request.setLogin(true);

        // 注入密钥
        request.setSecret(token.getSecret());
        request.setPrivateScret(token.getPrivateScret());

        return token;
    }

    // 参数解密，签名、时间差、客户端设备等验证
    private ApiRequest checkSignAndParams(ApiRequest apiRequest, ApiToken apiToken) throws ApiException {
        // 生成签名
        String sign = signatureService.sign(apiRequest);
        // 验证签名
        if (!sign.toUpperCase().equals(apiRequest.getSign())) {
            throw new ApiException(ApiCode.CHECK_SIGN_INVALID);
        }

        // 解密params参数值
        if (!StringUtils.isEmpty(apiRequest.getParams())) {
            decryptParams(apiRequest);
        }

        // 时间差校验
        long diffTime = Math.abs(Long.valueOf(apiRequest.getTimestamp()) - System.currentTimeMillis());
        if (apiProperties.isCkeckTimestamp() && diffTime > apiProperties.getTimestampDiffer()) {
            throw new ApiException(ApiCode.CHECK_TIME_INVALID);
        }

        // 客户端设备校验
        if (apiProperties.isCheckDevice()) {
            if (apiToken != null && !StringUtils.isEmpty(apiToken.getClientType())
                    && !StringUtils.isEmpty(apiToken.getClientCode())
                    && (!apiToken.getClientType().equals(apiRequest.getClientType())
                    || !apiToken.getClientCode().equals(apiRequest.getClientCode()))) {
                throw new ApiException(ApiCode.CHECK_DEVICE_INVALID);
            }
        }

        // 可根据request.getClientIp()扩展IP校验

        return apiRequest;
    }

    private void decryptParams(ApiRequest apiRequest) throws ApiException {
        try {
            if (apiProperties.getEnctyptType() == EnctyptType.AES) {
                String temp = apiRequest.getParams();
                temp = AESUtils.decryptStringFromBase64(temp, apiRequest.getSecret());
                apiRequest.setParams(temp);
            } else if (apiProperties.getEnctyptType() == EnctyptType.RSA) {
                String privateKey = apiRequest.getPrivateScret();
                String temp = apiRequest.getParams();
                temp = RSAUtils.decryptByPrivateKey(privateKey, temp);
                apiRequest.setParams(temp);
            } else if (apiProperties.getEnctyptType() == EnctyptType.BASE64) {
                String temp = apiRequest.getParams();
                temp = new String(Base64Utils.decodeFromString(temp));
                apiRequest.setParams(temp);
            }
        } catch (Exception e) {
            throw new ApiException(ApiCode.CHECK_ENCRYPT_INVALID);
        }
    }

    private ApiRegisterCenter.ApiRunnable valdateSysParams(HttpServletRequest request) throws ApiException {
        String apiName = request.getParameter(ApiParam.API_NAME);
        String apiParams = request.getParameter(ApiParam.API_PARAMS);
        String apiToken = request.getParameter(ApiParam.API_TOKEN);
        String apiSign = request.getParameter(ApiParam.API_SIGN);

        ApiRegisterCenter.ApiRunnable api;
        if (StringUtils.isEmpty(apiName)) {
            throw new ApiException(ApiCode.API_NAME_NULL);
        } else if (StringUtils.isEmpty(apiToken)) {
            throw new ApiException(ApiCode.API_TOKEN_NULL);
        } else if (StringUtils.isEmpty(apiSign)) {
            throw new ApiException(ApiCode.API_SIGN_NULL);
        } else if ((api = apiRegisterCenter.findApiRunnable(apiName)) == null) {
            throw new ApiException(ApiCode.API_UNEXIST);
        }

        if (api.getApiMapping().needParams() && StringUtils.isEmpty(apiParams)) {
            throw new ApiException(ApiCode.API_PARAMS_NULL);
        }

        return api;
    }

    /***
     * 验证业务参数，和构建业务参数对象
     */
    private Object[] buildParams(ApiRegisterCenter.ApiRunnable apiRunnable, String paramJson, HttpServletRequest request,
                                 HttpServletResponse response, ApiRequest apiRequest) throws ApiException {
        Map<String, Object> map;
        try {
            map = JsonUtils.toMap(paramJson);
        } catch (Exception e) {
            throw new ApiException(ApiCode.CHECK_PARAMS_INVALID);
        }
        if (map == null) {
            map = new HashMap<>();
        }

        Method method = apiRunnable.getTargetMethod();
        List<String> paramNames = Arrays.asList(parameterUtils.getParameterNames(method));
        Class<?>[] paramTypes = method.getParameterTypes();

        /*for (Map.Entry<String, Object> m : map.entrySet()) {
            if (!paramNames.contains(m.getKey())) {
                throw new ApiException("调用失败：接口不存在‘" + m.getKey() + "’参数");
            }
        }*/

        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i].isAssignableFrom(HttpServletRequest.class)) {
                args[i] = request;
            } else if (paramTypes[i].isAssignableFrom(ApiRequest.class)) {
                args[i] = apiRequest;
            } else if (map.containsKey(paramNames.get(i))) {
                try {
                    args[i] = convertJsonToBean(map.get(paramNames.get(i)), paramTypes[i]);
                } catch (Exception e) {
                    throw new ApiException("调用失败：‘" + paramNames.get(i) + "’参数格式或值错误："
                            + e.getMessage());
                }
            } else {
                args[i] = null;
            }
        }
        return args;
    }

    // 将MAP转换成具体的目标方方法参数对象
    private <T> Object convertJsonToBean(Object val, Class<T> targetClass) throws Exception {
        Object result = null;
        if (val == null) {
            return null;
        } else if (Integer.class.equals(targetClass)) {
            result = Integer.parseInt(val.toString());
        } else if (Long.class.equals(targetClass)) {
            result = Long.parseLong(val.toString());
        } else if (Date.class.equals(targetClass)) {
            if (val.toString().matches("[0-9]+")) {
                result = new Date(Long.parseLong(val.toString()));
            } else {
                throw new IllegalArgumentException("日期必须是长整型的时间戳");
            }
        } else if (String.class.equals(targetClass)) {
            if (val instanceof String) {
                result = val;
            } else {
                result = val.toString();
            }
        } else {
            result = JsonUtils.convertValue(val, targetClass);
        }
        return result;
    }

    private void returnResult(Object result, HttpServletResponse response) {
        try {
            JsonUtils.getObjectMapper().configure(
                    SerializationFeature.WRITE_NULL_MAP_VALUES, true);
            String json = JsonUtils.writeValueAsString(result);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            if (json != null) {
                response.getWriter().write(json);
            }
        } catch (IOException e) {
            logger.error("服务中心响应异常", e);
            throw new RuntimeException(e);
        }
    }
}
