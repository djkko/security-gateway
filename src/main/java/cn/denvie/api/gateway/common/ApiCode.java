package cn.denvie.api.gateway.common;

/**
 * Api响应码Enum。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public enum ApiCode {

    SUCCESS("10000", "执行成功"),
    LOGIN_ERROR("10200", "用户名或密码错误"),
    LOGIN_UNEXIST("10201", "用户不存在"),
    UN_LOGIN("10299", "用户未登陆"),
    CHECK_TOKEN_NULL("10300", "验证失败：'ApiToken'不存在"),
    CHECK_TOKEN_INVALID("10301", "验证失败：'ApiToken'已失效"),
    CHECK_SIGN_INVALID("10302", "验证失败：非法签名"),
    CHECK_TIME_INVALID("10303", "验证失败：签名失效"),
    CHECK_DEVICE_INVALID("10304", "验证失败：非法设备"),
    CHECK_ENCRYPT_INVALID("10305", "验证失败：参数解密失败"),
    API_NAME_NULL("10400", "调用失败：参数'name'为空"),
    API_PARAMS_NULL("10401", "调用失败：参数'params'为空"),
    API_TOKEN_NULL("10401", "调用失败：参数'token'为空"),
    API_SIGN_NULL("10401", "调用失败：参数'sign'为空"),
    API_UNEXIST("10402", "调用失败：指定API不存在"),
    COMMON_ERROR("99999", "执行失败");

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
