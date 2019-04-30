package cn.denvie.api.gateway.common;

/**
 * API网关异常。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class ApiException extends Exception {

    private String code;
    private String desc;

    public ApiException(ApiCode apiCode) {
        this.code = apiCode.code();
        this.desc = apiCode.message();
    }

    public ApiException(String desc) {
        this.code = ApiCode.FAILURE.code();
        this.desc = desc;
    }

    public ApiException(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
