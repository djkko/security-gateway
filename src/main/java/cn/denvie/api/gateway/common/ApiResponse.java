package cn.denvie.api.gateway.common;

import java.io.Serializable;

/**
 * Api请求响应结果。
 *
 * @param <Data> 响应的数据实体
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class ApiResponse<Data> implements Serializable {

    private static final long serialVersionUID = 5306504433449320455L;

    private String code;
    private String message;
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
