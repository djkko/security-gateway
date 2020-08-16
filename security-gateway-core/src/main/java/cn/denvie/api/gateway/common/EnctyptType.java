package cn.denvie.api.gateway.common;

/**
 * 加密方式。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public enum  EnctyptType {

    /**
     * 不加密
     */
    NONE,
    /**
     * Base64加密
     */
    BASE64,
    /**
     * AES加密
     */
    AES,
    /**
     * RSA加密
     */
    RSA

}
