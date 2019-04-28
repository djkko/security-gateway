package cn.denvie.api.gateway.common;

/**
 * Api响应码Enum。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public enum ApiCode {

    SUCCESS("100", "执行成功"),
    LOGIN_ERROR("200", "用户名或密码错误"),
    LOGIN_UNEXIST("201", "用户不存在"),
    UN_LOGIN("299", "用户未登陆"),
    CHECK_TOKEN_NULL("300", "验证失败：'ApiToken'不存在"),
    CHECK_TOKEN_INVALID("301", "验证失败：'ApiToken'已失效"),
    CHECK_SIGN_INVALID("302", "验证失败：非法签名"),
    CHECK_TIME_INVALID("303", "验证失败：签名失效"),
    CHECK_DEVICE_INVALID("304", "验证失败：非法设备"),
    CHECK_ENCRYPT_INVALID("305", "验证失败：参数解密失败"),
    API_NAME_NULL("400", "调用失败：参数'name'为空"),
    API_PARAMS_NULL("401", "调用失败：参数'params'为空"),
    API_TOKEN_NULL("401", "调用失败：参数'token'为空"),
    API_SIGN_NULL("401", "调用失败：参数'sign'为空"),
    API_UNEXIST("402", "调用失败：指定API不存在"),
    COMMON_ERROR("999", "执行失败");

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
