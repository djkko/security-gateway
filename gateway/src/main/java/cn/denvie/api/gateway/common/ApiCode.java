package cn.denvie.api.gateway.common;

/**
 * Api响应码Enum。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public enum ApiCode {

    SUCCESS("10000", "执行成功"),
    FAILURE("99999", "执行失败"),
    // NOT_SUPPORT_METHOD("10100", "请求失败，不支持的HTTP请求类型"),
    TOKEN_PARAM_NULL("10200", "Token创建失败，参数为空"),
    TOKEN_PARAM_USER_ID_NULL("10201", "Token创建失败，用户Id为空"),
    TOKEN_PARAM_USER_NAME_NULL("10202", "Token创建失败，用户名为空"),
    TOKEN_PARAM_CLIENT_TYPE_NULL("10203", "Token创建失败，客户端类型为空"),
    TOKEN_PARAM_CLIENT_CODE_NULL("10204", "Token创建失败，客户端设备ID为空"),
    TOKEN_SECRET_KEY_CREATE_ERROR("10205", "Token创建失败，密钥生成失败"),
    TOKEN_SAVE_TO_DB_ERROR("10206", "Token创建失败，数据库存储失败"),
    TOKEN_DUPLICATE_LOGIN("10207", "Token创建失败，用户已在其他设备登录"),
    CHECK_TOKEN_UN_LOGIN("10300", "未登陆"),
    CHECK_TOKEN_NULL("10301", "Token不存在"),
    CHECK_TOKEN_INVALID("10302", "Token已失效"),
    CHECK_SIGN_INVALID("10303", "非法签名"),
    CHECK_TIME_INVALID("10304", "签名已失效"),
    CHECK_DEVICE_INVALID("10305", "非法设备"),
    CHECK_ENCRYPT_INVALID("10306", "参数解密失败"),
    CHECK_PARAMS_INVALID("10307", "参数格式异常"),
    CHECK_ILLEGAL_REQUEST("10308", "非法请求"),
    CHECK_ILLEGAL_TIME("10309", "时间校验失败"),
    API_NAME_NULL("10400", "执行失败，接口名为空"),
    API_UN_EXIST("10401", "执行失败，接口不存在"),
    API_TOKEN_NULL("10402", "执行失败，Token为空"),
    API_SIGN_NULL("10403", "执行失败，签名为空"),
    CLIENT_PARAM_ERROR("10500", "ApiClient请求失败，参数错误"),
    CLIENT_API_URL_NULL("10501", "ApiClient请求失败，调用服务地址未指定"),
    CLIENT_API_NAME_NULL("10502", "ApiClient请求失败，调用接口名称未指定"),
    CLIENT_SECRET_NULL("10503", "ApiClient请求失败，私钥未指定");

    private String code;
    private String message;

    ApiCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

}
