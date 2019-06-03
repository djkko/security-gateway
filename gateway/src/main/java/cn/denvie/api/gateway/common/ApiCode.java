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
    NOT_SUPPORT_METHOD("10100", "请求失败，不支持的HTTP请求类型"),
    TOKEN_PARAM_NULL("10200", "Token创建失败，‘TokenParam’参数为空"),
    TOKEN_PARAM_USER_ID_NULL("10201", "Token创建失败，‘userId’参数为空"),
    TOKEN_PARAM_USER_NAME_NULL("10202", "Token创建失败，‘userName’参数为空"),
    TOKEN_PARAM_CLIENT_TYPE_NULL("10203", "Token创建失败，‘clientType’参数为空"),
    TOKEN_PARAM_CLIENT_CODE_NULL("10204", "Token创建失败，‘clientCode’参数为空"),
    TOKEN_SECRET_KEY_CREATE_ERROR("10205", "Token创建失败，密钥生成失败"),
    TOKEN_SAVE_TO_DB_ERROR("10206", "Token创建失败，数据库存储失败"),
    TOKEN_DUPLICATE_LOGIN("10207", "Token创建失败，用户已在其他设备登录"),
    TOKEN_UN_LOGIN("10208", "用户未登陆"),
    CHECK_TOKEN_NULL("10300", "验证失败：‘ApiToken’不存在"),
    CHECK_TOKEN_INVALID("10301", "验证失败：‘ApiToken’已失效"),
    CHECK_SIGN_INVALID("10302", "验证失败：非法签名"),
    CHECK_TIME_INVALID("10303", "验证失败：签名失效"),
    CHECK_DEVICE_INVALID("10304", "验证失败：非法设备"),
    CHECK_ENCRYPT_INVALID("10305", "验证失败：参数解密失败"),
    CHECK_PARAMS_INVALID("10306", "验证失败：‘params’参数格式异常"),
    API_NAME_NULL("10400", "调用失败：参数'name'为空"),
    API_PARAMS_NULL("10401", "调用失败：参数‘params’为空"),
    API_TOKEN_NULL("10401", "调用失败：参数‘token’为空"),
    API_SIGN_NULL("10401", "调用失败：参数‘sign’为空"),
    API_UNEXIST("10402", "调用失败：指定API不存在");

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
