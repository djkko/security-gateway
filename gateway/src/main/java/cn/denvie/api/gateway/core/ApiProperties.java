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
    private EnctyptType enctyptType = ApiConfig.ENCTYPT_TYPE;
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

    public EnctyptType getEnctyptType() {
        return enctyptType;
    }

    public void setEnctyptType(EnctyptType enctyptType) {
        this.enctyptType = enctyptType;
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
}
