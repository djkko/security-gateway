package cn.denvie.api.gateway.core;

import cn.denvie.api.gateway.common.EnctyptType;
import cn.denvie.api.gateway.common.MultiDeviceLogin;
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
    private boolean ckeckTimestamp = ApiConfig.TIMESTAMP_CHECK_ENABLE;
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

    public EnctyptType getEnctyptType() {
        return enctyptType;
    }

    public void setEnctyptType(EnctyptType enctyptType) {
        this.enctyptType = enctyptType;
    }

    public boolean isCkeckTimestamp() {
        return ckeckTimestamp;
    }

    public void setCkeckTimestamp(boolean ckeckTimestamp) {
        this.ckeckTimestamp = ckeckTimestamp;
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
}
