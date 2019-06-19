package cn.denvie.api.gateway.core;

import cn.denvie.api.gateway.common.EnctyptType;
import cn.denvie.api.gateway.common.MultiDeviceLogin;
import cn.denvie.api.gateway.common.ParamType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Api属性配置。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "cn.denvie.api")
public class ApiProperties {

    /**
     * 加密方式
     */
    private EnctyptType encryptType = ApiConfig.ENCTYPT_TYPE;
    /**
     * 是否启用客户端与服务端时间差校验
     */
    private boolean checkTimestamp = ApiConfig.TIMESTAMP_CHECK_ENABLE;
    /**
     * 允许的客户端请求时间与服务端时间差
     */
    private long timestampDiffer = ApiConfig.TIMESTAMP_DIFFER;
    /**
     * 是否启用客户端设备校验
     */
    private boolean checkDevice = ApiConfig.TIMESTAMP_DEVICE_ENABLE;
    /**
     * Token的有效期（毫秒）
     */
    private long tokenValidTime = ApiConfig.TOKEN_VAlID_TIME;
    /**
     * 多设备登录策略
     */
    private MultiDeviceLogin multiDeviceLogin = ApiConfig.MULTI_DEVICE_LOGIN;
    /**
     * AES加密算法密钥（16位），若不配置，则自动生成
     */
    private String aesKey = null;
    /**
     * RSA加密算法公钥，若不配置，则自动生成
     */
    private String rsaPublicKey = null;
    /**
     * RSA加密算法私钥，若不配置，则自动生成
     */
    private String rsaPrivateKey = null;
    /**
     * 传参方式，默认为：BODY
     */
    private ParamType paramType = ApiConfig.DEFAULT_PARAM_TYPE;
    /**
     * 是否开户日志打印
     */
    private boolean enableLogging = ApiConfig.ENABLE_LOGGING;
    /**
     * Sub Api 的AES私钥或者RSA公钥
     */
    private String subSecret = ApiConfig.SUB_SECRET;
    /**
     * Sub Api 的RSA私钥
     */
    private String subPrivateSecret = "";
    /**
     * Rest Client 调用的接口路径
     */
    private String clientBaseUrl;
    /**
     * Rest Client 参数加密的私钥
     */
    private String clientSecret;
    /**
     * Rest Client 读取超时时间
     */
    private int clientReadTimeout = ApiConfig.CLIENT_READ_TIMEOUT;
    /**
     * Rest Client 连接超时时间
     */
    private int clientConnectTimeout = ApiConfig.CLIENT_CONNECT_TIMEOUT;
    /**
     * Rest Client 请求编码
     */
    private String clientRequestCharset = ApiConfig.CLIENT_REQUEST_CHARSET;

    public EnctyptType getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(EnctyptType encryptType) {
        this.encryptType = encryptType;
    }

    public boolean isCheckTimestamp() {
        return checkTimestamp;
    }

    public void setCheckTimestamp(boolean checkTimestamp) {
        this.checkTimestamp = checkTimestamp;
    }

    public long getTimestampDiffer() {
        return timestampDiffer;
    }

    public void setTimestampDiffer(long timestampDiffer) {
        this.timestampDiffer = timestampDiffer;
    }

    public boolean isCheckDevice() {
        return checkDevice;
    }

    public void setCheckDevice(boolean checkDevice) {
        this.checkDevice = checkDevice;
    }

    public long getTokenValidTime() {
        return tokenValidTime;
    }

    public void setTokenValidTime(long tokenValidTime) {
        this.tokenValidTime = tokenValidTime;
    }

    public MultiDeviceLogin getMultiDeviceLogin() {
        return multiDeviceLogin;
    }

    public void setMultiDeviceLogin(MultiDeviceLogin multiDeviceLogin) {
        this.multiDeviceLogin = multiDeviceLogin;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getRsaPublicKey() {
        return rsaPublicKey;
    }

    public void setRsaPublicKey(String rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    public String getRsaPrivateKey() {
        return rsaPrivateKey;
    }

    public void setRsaPrivateKey(String rsaPrivateKey) {
        this.rsaPrivateKey = rsaPrivateKey;
    }

    public ParamType getParamType() {
        return paramType;
    }

    public void setParamType(ParamType paramType) {
        this.paramType = paramType;
    }

    public boolean isEnableLogging() {
        return enableLogging;
    }

    public void setEnableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }

    public String getSubSecret() {
        return subSecret;
    }

    public void setSubSecret(String subSecret) {
        this.subSecret = subSecret;
    }

    public String getSubPrivateSecret() {
        return subPrivateSecret;
    }

    public void setSubPrivateSecret(String subPrivateSecret) {
        this.subPrivateSecret = subPrivateSecret;
    }

    public String getClientBaseUrl() {
        return clientBaseUrl;
    }

    public void setClientBaseUrl(String clientBaseUrl) {
        this.clientBaseUrl = clientBaseUrl;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public int getClientReadTimeout() {
        return clientReadTimeout;
    }

    public void setClientReadTimeout(int clientReadTimeout) {
        this.clientReadTimeout = clientReadTimeout;
    }

    public int getClientConnectTimeout() {
        return clientConnectTimeout;
    }

    public void setClientConnectTimeout(int clientConnectTimeout) {
        this.clientConnectTimeout = clientConnectTimeout;
    }

    public String getClientRequestCharset() {
        return clientRequestCharset;
    }

    public void setClientRequestCharset(String clientRequestCharset) {
        this.clientRequestCharset = clientRequestCharset;
    }
}
