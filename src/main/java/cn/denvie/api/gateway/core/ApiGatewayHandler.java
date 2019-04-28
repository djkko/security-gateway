package cn.denvie.api.gateway.core;

import cn.denvie.api.gateway.common.*;
import cn.denvie.api.gateway.service.ResponseService;
import cn.denvie.api.gateway.service.TokenService;
import cn.denvie.api.gateway.utils.AESUtils;
import cn.denvie.api.gateway.utils.JsonUtils;
import cn.denvie.api.gateway.utils.MD5Utils;
import cn.denvie.api.gateway.utils.RSAUtils;
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
        ApiResponse<?> result;
        ApiRegisterCenter.ApiRunnable apiRun;
        ApiRequest apiRequest;
        try {
            // 系统参数验证
            apiRun = valdateSysParams(request);
            // 构建ApiRequest
            apiRequest = buildApiRequest(request);
            // 验证Token
            if (apiRequest.getAccessToken() != null) {
                checkToken(apiRequest);
            }
            // 验证签名和参数
            checkSignAndParams(apiRequest);
            // 登录验证
            if (apiRun.getApiMapping().needLogin()) {
                if (!apiRequest.isLogin()) {
                    throw new ApiException(ApiCode.UN_LOGIN);
                }
            }

            Object[] args = buildParams(apiRun, apiRequest.getParams(), request, response, apiRequest);
            logger.info("请求接口【" + apiName + "】, 参数=" + apiRequest.getParams());
            result = responseService.success(apiRun.run(args));
        } catch (ApiException e) {
            logger.error("调用接口【" + apiName + "】异常, " + e.getMessage() + "，参数=" + apiParams/*, e*/);
            result = responseService.error(e.getCode(), e.getMessage(), null);
        } catch (InvocationTargetException e) {
            logger.error("调用接口【" + apiName + "】异常, 参数=" + apiParams, e.getTargetException());
            ApiException apiException = new ApiException(e.getMessage());
            result = responseService.error(apiException.getCode(), apiException.getMessage(), null);
        } catch (Exception e) {
            logger.error("其他异常", e);
            ApiException apiException = new ApiException(e.getMessage());
            result = responseService.error(apiException.getCode(), apiException.getMessage(), null);
        }

        // 统一返回结果
        returnResult(result, response);
    }

    private ApiRequest buildApiRequest(HttpServletRequest request) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setApiName(request.getParameter(ApiParam.API_NAME));
        apiRequest.setParams(request.getParameter(ApiParam.API_PARAMS));
        apiRequest.setAccessToken(request.getParameter(ApiParam.API_TOKEN));
        apiRequest.setClientType(request.getParameter(ApiParam.API_CLIENT_TYPE));
        apiRequest.setClientType(request.getParameter(ApiParam.API_CLIENT_CODE));
        apiRequest.setTimestamp(request.getParameter(ApiParam.API_TIMESTAMP));
        apiRequest.setSign(request.getParameter(ApiParam.API_SIGN));
        return apiRequest;
    }

    // 验证token
    private ApiRequest checkToken(ApiRequest request) throws ApiException {
        // 验证Token
        ApiToken token = tokenService.getToken(request.getAccessToken());
        if (token == null) {
            throw new ApiException(ApiCode.CHECK_TOKEN_NULL);
        }
        if (new Date(token.getExpireTime()).before(new Date())) {
            throw new ApiException(ApiCode.CHECK_TOKEN_INVALID);
        }

        // 注入密钥
        request.setSecret(token.getSecret());
        request.setPrivateScret(token.getPrivateScret());

        return request;
    }

    // 验证签名和参数
    private ApiRequest checkSignAndParams(ApiRequest request) throws ApiException {
        // 解密params参数值
        if (ApiConfig.ENCTYPT_TYPE == EnctyptType.AES) {
            try {
                String temp = request.getParams();
                temp = AESUtils.decryptString(temp, request.getSecret());
                request.setParams(temp);
            } catch (Exception e) {
                throw new ApiException(ApiCode.CHECK_ENCRYPT_INVALID);
            }
        } else if (ApiConfig.ENCTYPT_TYPE == EnctyptType.RSA) {
            try {
                String privateKey = request.getPrivateScret();
                String temp = request.getParams();
                temp = RSAUtils.decryptByPrivateKey(privateKey, temp);
                request.setParams(temp);
            } catch (Exception e) {
                throw new ApiException(ApiCode.CHECK_ENCRYPT_INVALID);
            }
        } else if (ApiConfig.ENCTYPT_TYPE == EnctyptType.BASE64) {
            try {
                String temp = request.getParams();
                temp = new String(Base64Utils.decodeFromString(temp));
                request.setParams(temp);
            } catch (Exception e) {
                throw new ApiException(ApiCode.CHECK_ENCRYPT_INVALID);
            }
        }

        // 生成签名
        String apiName = request.getApiName();
        String accessToken = request.getAccessToken();
        String secret = request.getSecret();
        String params = request.getParams();
        String timestamp = request.getTimestamp();
        String key = secret + apiName + params + accessToken + timestamp + secret;
        String sign = MD5Utils.md5(key);

        if (!sign.toUpperCase().equals(request.getSign())) {
            throw new ApiException(ApiCode.CHECK_SIGN_INVALID);
        }

        // 时间校验
        if (ApiConfig.TIMESTAMP_ENABLE
                && Math.abs(Long.valueOf(timestamp) - System.currentTimeMillis()) > ApiConfig.TIMESTAMP_DIFFER) {
            throw new ApiException(ApiCode.CHECK_TIME_INVALID);
        }

        // 可根据request.getClientType()和request.getClientCode()扩展设备校验

        // 可根据request.getClientIp()扩展IP校验

        request.setLogin(true);
        request.setMemberId(request.getMemberId());

        return request;
    }

    private ApiRegisterCenter.ApiRunnable valdateSysParams(HttpServletRequest request) throws ApiException {
        String apiName = request.getParameter(ApiParam.API_NAME);
        String apiParams = request.getParameter(ApiParam.API_PARAMS);
        String apiToken = request.getParameter(ApiParam.API_TOKEN);
        String apiSign = request.getParameter(ApiParam.API_SIGN);

        ApiRegisterCenter.ApiRunnable api;
        if (StringUtils.isEmpty(apiName)) {
            throw new ApiException(ApiCode.API_NAME_NULL);
        } else if (StringUtils.isEmpty(apiParams)) {
            throw new ApiException(ApiCode.API_PARAMS_NULL);
        } else if (StringUtils.isEmpty(apiToken)) {
            throw new ApiException(ApiCode.API_TOKEN_NULL);
        } else if (StringUtils.isEmpty(apiSign)) {
            throw new ApiException(ApiCode.API_SIGN_NULL);
        } else if ((api = apiRegisterCenter.findApiRunnable(apiName)) == null) {
            throw new ApiException(ApiCode.API_UNEXIST);
        }

        return api;
    }

    /***
     * 验证业务参数，和构建业务参数对象
     */
    private Object[] buildParams(ApiRegisterCenter.ApiRunnable apiRunnable, String paramJson, HttpServletRequest request,
                                 HttpServletResponse response, ApiRequest apiRequest) throws ApiException {
        Map<String, Object> map = null;
        try {
            map = JsonUtils.toMap(paramJson);
        } catch (Exception e) {
            throw new ApiException("调用失败：json字符串格式异常，请检查params参数");
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
                    throw new ApiException("调用失败：指定参数格式错误或值错误‘" + paramNames.get(i) + "’"
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
