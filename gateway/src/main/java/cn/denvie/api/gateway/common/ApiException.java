package cn.denvie.api.gateway.common;

/**
 * API网关异常。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class ApiException extends Exception {

    private String code;

    public ApiException(ApiCode apiCode) {
        super(apiCode.message());
        this.code = apiCode.code();
    }

    public ApiException(String message) {
        super(message);
        code = ApiCode.COMMON_ERROR.code();
    }

    public String getCode() {
        return code;
    }

}
