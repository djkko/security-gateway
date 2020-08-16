package cn.denvie.api.gateway.common;

/**
 * 多设备登录策略。
 *
 * @author DengZhaoyong
 * @version 1.2.0
 */
public enum MultiDeviceLogin {
    /**
     * 允许多设备登录
     */
    ALLOW,
    /**
     * 将旧用户挤掉
     */
    REPLACE,
    /**
     * 用户已在一台设备登录，并且Token未失效，拒绝再次登录
     */
    REFUSE
}
