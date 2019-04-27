package cn.denvie.api.gateway.core;

import cn.denvie.api.gateway.common.EnctyptType;

/**
 * Api配置。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class ApiConfig {

    /**
     * 加密方式
     */
    public static final EnctyptType ENCTYPT_TYPE = EnctyptType.AES;

    /**
     * 是否启用客户端与服务端时间差校验
     */
    public static final boolean TIMESTAMP_ENABLE = true;

    /**
     * 允许的客户端请求时间与服务端时间差
     */
    public static final long TIMESTAMP_DIFFER = 10 * 60 * 1000;

    /**
     * Token的有效期（毫秒）
     */
    public static final long EXPIRE_TIME = 7 * 24 * 3600 * 1000;

}
