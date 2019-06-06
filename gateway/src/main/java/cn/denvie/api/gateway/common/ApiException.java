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
        this.code = ApiCode.FAILURE.code();
    }

    public ApiException(String code, String message) {
        super(message);
        this.code = message;
    }

    public String getCode() {
        return code;
    }

    public int getCodeInt() {
        try {
            return Integer.parseInt(code);
        } catch (NumberFormatException e) {
            return Integer.parseInt(ApiCode.FAILURE.code());
        }
    }

}
