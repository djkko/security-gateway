package cn.denvie.api.gateway.common;

/**
 * 调用方使用的Code。
 *
 * @author DengZhaoyong
 * @version 1.2.5
 */
public class InvokeCode {

    private String code;
    private String message;

    public InvokeCode() {
    }

    public InvokeCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
