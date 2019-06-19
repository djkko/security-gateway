package cn.denvie.api.gateway.client;

import cn.denvie.api.gateway.utils.AESUtils;
import cn.denvie.api.gateway.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * API安全网关接口调用参数。
 *
 * @author DengZhaoyong
 * @version 1.3.0
 * @date 2019/6/19
 */
@Slf4j
public class InvokeParam implements Serializable {

    private static final long serialVersionUID = 590076163542070926L;

    private String baseUrl;
    private String name;
    private String params;
    private String timestamp;
    private String sign;

    private InvokeParam() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getName() {
        return name;
    }

    public String getParams() {
        return params;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "InvokeParam{" +
                "baseUrl='" + baseUrl + '\'' +
                ", name='" + name + '\'' +
                ", params='" + params + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    ///////////////////////////////////////////////////////////////////////////
    // ApiParam Builder
    ///////////////////////////////////////////////////////////////////////////

    public static class Builder {

        // 接口地址
        private String baseUrl;
        // API接口名称
        private String name;
        // 加密的私钥
        private String secret;
        // 自定义签名
        private String sign;
        // 参数
        private Map<String, Serializable> paramMap = new HashMap<>();
        // Header
        private Map<String, String> headerMap = new HashMap<>();

        /**
         * @param name API接口名称
         */
        public Builder(String name) {
            this.name = name;
        }

        /**
         * @param baseUrl 接口地址
         * @param name    API接口名称
         */
        public Builder(String baseUrl, String name) {
            this.baseUrl = baseUrl;
            this.name = name;
        }

        public String name() {
            return this.name;
        }

        public String baseUrl() {
            return this.baseUrl;
        }

        /**
         * 若接口地址不指定，默认从配置文件的cn.denvie.api.client-base-url属性读取
         *
         * @param baseUrl 接口地址
         * @return
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public String secret() {
            return this.secret;
        }

        /**
         * 若私钥不指定，默认从配置文件的cn.denvie.api.client-secret属性读取
         *
         * @param secret 加密的私钥
         * @return
         */
        public Builder secret(String secret) {
            this.secret = secret;
            return this;
        }

        public String sign() {
            return this.sign;
        }

        /**
         * @param sign 签名
         * @return
         */
        public Builder sign(String sign) {
            this.sign = sign;
            return this;
        }

        public Builder addParam(String name, Serializable value) {
            this.paramMap.put(name, value);
            return this;
        }

        public Builder addHeader(String name, String value) {
            this.headerMap.put(name, value);
            return this;
        }

        public InvokeParam build() {
            InvokeParam invokeParam = new InvokeParam();
            invokeParam.baseUrl = this.baseUrl;
            invokeParam.name = this.name;
            invokeParam.timestamp = System.currentTimeMillis() + "";
            // 参数加密
            String json = JsonUtils.writeValueAsString(paramMap);
            try {
                invokeParam.params = AESUtils.encryptToBase64(json, secret);
            } catch (Exception e) {
                log.error(e.toString());
            }
            // 使用外部传入的签名
            invokeParam.sign = sign;
            return invokeParam;
        }
    }

}
