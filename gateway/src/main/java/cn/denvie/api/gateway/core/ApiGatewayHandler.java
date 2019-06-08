package cn.denvie.api.gateway.core;

import cn.denvie.api.gateway.common.*;
import cn.denvie.api.gateway.service.ApiTokenService;
import cn.denvie.api.gateway.service.InvokeExceptionHandler;
import cn.denvie.api.gateway.service.ResponseService;
import cn.denvie.api.gateway.service.SignatureService;
import cn.denvie.api.gateway.utils.AESUtils;
import cn.denvie.api.gateway.utils.JsonUtils;
import cn.denvie.api.gateway.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * API请求处理器。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
@Component
@Slf4j
public class ApiGatewayHandler implements InitializingBean, ApplicationContextAware {

    @Autowired
    private ApiTokenService apiTokenService;
    @Autowired
    private ResponseService responseService;
    @Autowired
    private SignatureService signatureService;
    @Autowired
    InvokeExceptionHandler invokeExceptionHandler;
    @Autowired
    ApiProperties apiProperties;
    @Autowired
    private MethodParamValidator methodParamValidator;
    @Autowired
    private ApiInvokeInterceptor apiInvokeInterceptor;

    private ParameterNameDiscoverer parameterNameDiscoverer;
    private ApiRegisterCenter apiRegisterCenter;

    public ApiGatewayHandler() {
        parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        apiRegisterCenter = new ApiRegisterCenter(context);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        apiRegisterCenter.loadApiFromSpringBeans();
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, String httpMethod) {
        ApiParam originalApiParam = resolveApiParam(request);
        ApiResponse apiResponse;
        ApiRegisterCenter.ApiRunnable apiRunnable;
        ApiRequest apiRequest = null;
        ApiToken apiToken = null;
        try {
            // 系统参数验证
            apiRunnable = validateSysParams(originalApiParam);
            // 构建ApiRequest
            apiRequest = buildApiRequest(originalApiParam);
            // 验证Token
            if (!StringUtils.isEmpty(apiRequest.getAccessToken())) {
                apiToken = checkToken(apiRequest, apiRunnable.getApiMapping().needLogin());
                // 将token相关值设置到HttpServletRequest属性中
                request.setAttribute(ApiToken.REQ_PARAM_TOKEN, apiRequest.getAccessToken());
                request.setAttribute(ApiToken.REQ_PARAM_TOKEN_OBJ, apiToken);
            }
            // 验证签名和参数
            checkSignAndParams(apiRequest, apiToken);
            // 登录验证
            if (apiRunnable.getApiMapping().needLogin()) {
                if (!apiRequest.isLogin()) {
                    throw new ApiException(ApiCode.TOKEN_UN_LOGIN);
                }
            }

            Object[] args = buildParams(apiRunnable, apiRequest.getParams(), request, response, apiRequest);
            log.info(httpMethod + "调用接口【{}】，参数：{}", originalApiParam.getName(), apiRequest.getParams());
            apiInvokeInterceptor.before(apiRequest, args);
            Object resultData = apiRunnable.run(args);
            apiInvokeInterceptor.after(apiRequest, resultData);
            apiResponse = responseService.success(resultData);
        } catch (ApiException e) {
            log.error("调用接口【{}】异常：{}，参数：{}",
                    originalApiParam.getName(), e.getMessage(), originalApiParam.getParams()/*, e*/);
            apiInvokeInterceptor.error(apiRequest, e);
            apiResponse = responseService.error(e.getCode(), e.getMessage(), null);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException() == null ? e : e.getTargetException();
            String errMsg = t.getMessage();
            log.error("调用接口【{}】异常：{}，参数：{}",
                    originalApiParam.getName(), errMsg, originalApiParam.getParams()/*, e.getTargetException()*/);
            apiInvokeInterceptor.error(apiRequest, e);
            e.printStackTrace();
            apiResponse = invokeExceptionHandler.handle(apiRequest, t);
        } catch (Exception e) {
            log.error("调用接口【{}】异常：{}，参数：{}",
                    originalApiParam.getName(), e.toString(), originalApiParam.getParams());
            apiInvokeInterceptor.error(apiRequest, e);
            e.printStackTrace();
            apiResponse = invokeExceptionHandler.handle(apiRequest, e);
        }

        // 统一返回结果
        returnResult(apiResponse, response);
    }

    /**
     * 解析请求参数。
     */
    private ApiParam resolveApiParam(HttpServletRequest request) {
        ApiParam param = new ApiParam();
        if (apiProperties.getParamType() == ParamType.BODY) {
            param.inflateByBodyRequest(request);
        } else {
            param.inflateByFormRequest(request);
        }
        return param;
    }

    // 根据HttpServletRequest构建Api请求参数
    private ApiRequest buildApiRequest(ApiParam originalApiParam) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setApiName(originalApiParam.getName());
        String params = originalApiParam.getParams();
        if (params == null) {
            params = "";
        }
        apiRequest.setParams(params);
        apiRequest.setAccessToken(originalApiParam.getToken());
        apiRequest.setClientType(originalApiParam.getClientType());
        apiRequest.setClientCode(originalApiParam.getClientCode());
        apiRequest.setTimestamp(originalApiParam.getTimestamp());
        apiRequest.setSign(originalApiParam.getSign());

        return apiRequest;
    }

    // 验证Token
    private ApiToken checkToken(ApiRequest request, boolean needLogin) throws ApiException {
        // 验证Token
        ApiToken token = apiTokenService.getToken(request.getAccessToken());
        if (token == null) {
            if (needLogin) {
                throw new ApiException(ApiCode.CHECK_TOKEN_NULL);
            }
            return null;
        }
        if (token.isExpired()) {
            if (needLogin) {
                throw new ApiException(ApiCode.CHECK_TOKEN_INVALID);
            }
            return null;
        }

        // 注入用户信息
        request.setMemberId(token.getUserId());
        request.setLogin(true);

        // 注入密钥
        request.setSecret(token.getSecret());
        request.setPrivateScret(token.getPrivateSecret());

        return token;
    }

    // 参数解密，签名、时间差、客户端设备等验证
    private ApiRequest checkSignAndParams(ApiRequest apiRequest, ApiToken apiToken) throws ApiException {
        // 生成签名
        String sign = signatureService.sign(apiRequest);
        // 验证签名
        if (StringUtils.isEmpty(sign) || !sign.equals(apiRequest.getSign())) {
            throw new ApiException(ApiCode.CHECK_SIGN_INVALID);
        }

        // 解密params参数值
        if (!StringUtils.isEmpty(apiRequest.getParams())) {
            decryptParams(apiRequest);
        }

        // 时间差校验
        long diffTime = Math.abs(Long.valueOf(apiRequest.getTimestamp()) - System.currentTimeMillis());
        if (apiProperties.isCheckTimestamp() && diffTime > apiProperties.getTimestampDiffer()) {
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

    private ApiRegisterCenter.ApiRunnable validateSysParams(ApiParam originalApiParam) throws ApiException {
        ApiRegisterCenter.ApiRunnable api;
        if (StringUtils.isEmpty(originalApiParam.getName())) {
            throw new ApiException(ApiCode.API_NAME_NULL);
        } else if (StringUtils.isEmpty(originalApiParam.getToken())) {
            throw new ApiException(ApiCode.API_TOKEN_NULL);
        } else if (StringUtils.isEmpty(originalApiParam.getSign())) {
            throw new ApiException(ApiCode.API_SIGN_NULL);
        } else if ((api = apiRegisterCenter.findApiRunnable(originalApiParam.getName())) == null) {
            throw new ApiException(ApiCode.API_UNEXIST);
        }
        if (api.getApiMapping().needParams() && StringUtils.isEmpty(originalApiParam.getParams())) {
            throw new ApiException(ApiCode.API_PARAMS_NULL);
        }
        return api;
    }

    /***
     * 验证业务参数，和构建业务参数对象
     */
    private Object[] buildParams(ApiRegisterCenter.ApiRunnable apiRunnable, String paramJson, HttpServletRequest request,
                                 HttpServletResponse response, ApiRequest apiRequest) throws ApiException {
        Map<String, Object> paramMap;
        try {
            paramMap = JsonUtils.toMap(paramJson);
        } catch (Exception e) {
            throw new ApiException(ApiCode.CHECK_PARAMS_INVALID);
        }
        if (paramMap == null) {
            paramMap = new HashMap<>();
        }

        Method method = apiRunnable.getTargetMethod();
        // 优先通过@ApiMapping的paramNames属性获取参数名列表
        String[] paramNames = apiRunnable.getApiMapping().paramNames();
        log.info("{} Method ParameterNames：{}", method.getName(), paramNames);
        Class<?>[] paramTypes = method.getParameterTypes();
        // 如果paramNames未配置，则尝试通过ParameterNameDiscoverer获取
        if (getMethodArgumentLength(paramTypes) > 0 && (paramNames == null || paramNames.length == 0)) {
            paramNames = parameterNameDiscoverer.getParameterNames(method);
        }

        if (getMethodArgumentLength(paramTypes) > getMethodArgumentLength(paramNames)) {
            throw new ApiException("调用失败：参数名列表有误");
        }

        /*for (Map.Entry<String, Object> m : map.entrySet()) {
            if (!paramNames.contains(m.getKey())) {
                throw new ApiException("调用失败：接口不存在‘" + m.getKey() + "’参数");
            }
        }*/

        Object[] args = new Object[paramTypes.length];
        HandlerMethodArgumentResolver resolver;
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i].isAssignableFrom(HttpServletRequest.class)) {
                args[i] = request;
            } else if (paramTypes[i].isAssignableFrom(ApiRequest.class)) {
                args[i] = apiRequest;
            } else if (paramMap.containsKey(paramNames[i])) {
                try {
                    args[i] = convertJsonToBean(paramMap.get(paramNames[i]), paramTypes[i]);
                    // 验证带 @Valid 注解的Bean参数
                    /*if (!BeanUtils.isSimpleProperty(paramTypes[i])
                            && apiRunnable.getMethodParameters().get(i).hasParameterAnnotation(Valid.class)) {
                        validateParamValue(methodParamValidator.validate(args[i]));
                    }*/
                } catch (Exception e) {
                    throw new ApiException("调用失败：‘" + paramNames[i] + "’参数错误："
                            + e.getMessage());
                }
            } else if ((resolver = apiRegisterCenter.supportsParameter(apiRunnable.getMethodParameters().get(i))) != null) {
                try {
                    args[i] = resolver.resolveArgument(apiRunnable.getMethodParameters().get(i), null,
                            new ServletWebRequest(request), null);
                } catch (Exception e) {
                    throw new ApiException("调用失败：‘" + paramNames[i] + "’参数错误："
                            + e.getMessage());
                }
            } else {
                args[i] = null;
            }
        }

        // 验证方法中带 @Valid 注解的参数
        validateParamValue(methodParamValidator.validateParameters(apiRunnable.getTarget(), method, args));

        return args;
    }

    private void validateParamValue(Set<ConstraintViolation<Object>> constraintViolations) {
        if (constraintViolations != null && !constraintViolations.isEmpty()) {
            // @Valid 参数校验失败
            StringBuilder validMsgBuilder = new StringBuilder();
            for (ConstraintViolation<Object> violation : constraintViolations) {
                validMsgBuilder.append(violation.getMessage()).append(";");
            }
            validMsgBuilder.deleteCharAt(validMsgBuilder.length() - 1);
            throw new RuntimeException(validMsgBuilder.toString());
        }
    }

    private int getMethodArgumentLength(Object[] objs) {
        if (objs == null) return 0;
        return objs.length;
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
            String json = JsonUtils.writeValueAsString(result);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            if (json != null) {
                response.getWriter().write(json);
            }
        } catch (Exception e) {
            log.error("服务器响应异常：{}", e.toString());
            throw new RuntimeException(e);
        }
    }
}
