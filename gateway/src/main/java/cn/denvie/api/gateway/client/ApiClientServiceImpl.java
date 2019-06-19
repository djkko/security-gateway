package cn.denvie.api.gateway.client;

import cn.denvie.api.gateway.common.ApiCode;
import cn.denvie.api.gateway.common.ApiException;
import cn.denvie.api.gateway.core.ApiConfig;
import cn.denvie.api.gateway.core.ApiProperties;
import cn.denvie.api.gateway.core.ApiRequest;
import cn.denvie.api.gateway.service.SubSignatureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * API安全网关客户端服务实现。
 *
 * @author DengZhaoyong
 * @version 1.3.0
 * @date 2019/6/19
 */
@Service
@Slf4j
public class ApiClientServiceImpl implements ApiClientService {

    @Autowired
    private ApiProperties apiProperties;

    private SubSignatureService subSignatureService;
    private RestTemplate restTemplate;

    public ApiClientServiceImpl() {
        subSignatureService = new ApiConfig().subSignatureService();
    }

    @Override
    public String post(InvokeParam.Builder paramBuilder) throws ApiException {
        return post(paramBuilder, String.class);
    }

    @Override
    public <T> T post(InvokeParam.Builder paramBuilder, Class<T> clazz) throws ApiException {
        // 处理请求地址
        if (StringUtils.isEmpty(paramBuilder.baseUrl())) {
            paramBuilder.baseUrl(apiProperties.getClientBaseUrl());
        }
        // 处理私钥
        if (StringUtils.isEmpty(paramBuilder.secret())) {
            paramBuilder.secret(apiProperties.getClientSecret());
        }

        // 参数校验
        validate(paramBuilder);

        // 构建调用参数
        InvokeParam invokeParam = paramBuilder.build();

        // 传进来的签名为空，则使用SubSignatureService生成签名
        if (StringUtils.isEmpty(invokeParam.getSign())) {
            ApiRequest apiRequest = new ApiRequest();
            apiRequest.setApiName(invokeParam.getName());
            apiRequest.setParams(invokeParam.getParams());
            apiRequest.setSecret(paramBuilder.secret());
            apiRequest.setTimestamp(invokeParam.getTimestamp());
            String sign = subSignatureService.sign(apiRequest);
            invokeParam.setSign(sign);
        }

        // 发起请求
        ResponseEntity<T> responseEntity = getRestTemplate()
                .postForEntity(invokeParam.getBaseUrl(), invokeParam, clazz);

        return responseEntity.getBody();
    }

    private void validate(InvokeParam.Builder builder) throws ApiException {
        if (builder == null) {
            throw new ApiException(ApiCode.CLIENT_PARAM_ERROR);
        }
        if (StringUtils.isEmpty(builder.baseUrl())) {
            throw new ApiException(ApiCode.CLIENT_API_URL_NULL);
        }
        if (StringUtils.isEmpty(builder.name())) {
            throw new ApiException(ApiCode.CLIENT_API_NAME_NULL);
        }
        if (StringUtils.isEmpty(builder.secret())) {
            throw new ApiException(ApiCode.CLIENT_SECRET_NULL);
        }
    }

    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            synchronized (this) {
                if (restTemplate == null) {
                    // 设置超时时间
                    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                    requestFactory.setReadTimeout(apiProperties.getClientReadTimeout());
                    requestFactory.setConnectTimeout(apiProperties.getClientConnectTimeout());

                    // 添加转换器
                    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
                    messageConverters.add(new StringHttpMessageConverter(Charset.forName(apiProperties.getClientRequestCharset())));
                    messageConverters.add(new FormHttpMessageConverter());
                    messageConverters.add(new MappingJackson2HttpMessageConverter());

                    restTemplate = new RestTemplate(messageConverters);
                    restTemplate.setRequestFactory(requestFactory);
                    restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
                }
            }
        }
        return restTemplate;
    }

}
