package cn.denvie.api.gateway.common;

import cn.denvie.api.gateway.core.ApiGatewayHandler;
import cn.denvie.api.gateway.utils.HttpServletRequestReader;
import cn.denvie.api.gateway.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 统一的Api请求参数格式。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class ApiParam implements Serializable {

    private static final long serialVersionUID = -5973017396172235006L;

    private static final Logger logger = LoggerFactory.getLogger(ApiParam.class);

    public static final String API_NAME = "name";
    public static final String API_PARAMS = "params";
    public static final String API_TOKEN = "token";
    public static final String API_TOKEN_HEADER = "token";
    public static final String API_CLIENT_TYPE = "clientType";
    public static final String API_CLIENT_TYPE_HEADER = "clientType";
    public static final String API_CLIENT_CODE = "clientCode";
    public static final String API_CLIENT_CODE_HEADER = "clientDeviceId";
    public static final String API_TIMESTAMP = "timestamp";
    public static final String API_SIGN = "sign";

    private String name;
    private String params;
    private String token;
    private String clientType;
    private String clientCode;
    private String timestamp;
    private String sign;

    /**
     * 通过请求BODY填充参数。
     */
    public void inflateByBodyRequest(HttpServletRequest request) {
        String params = HttpServletRequestReader.readAsString(request);
        if (StringUtils.isEmpty(params)) {
            return;
        }
        try {
            ApiParam apiParam = JsonUtils.convertValue(params, ApiParam.class);
            if (apiParam != null) {
                this.setName(apiParam.getName());
                this.setParams(apiParam.getParams());
                this.setToken(apiParam.getToken());
                this.setClientType(apiParam.getClientType());
                this.setClientCode(apiParam.getClientCode());
                this.setTimestamp(apiParam.getTimestamp());
                this.setSign(apiParam.getSign());
            }
            if (StringUtils.isEmpty(this.getToken())) {
                this.setToken(request.getHeader(API_TOKEN_HEADER));
            }
            if (StringUtils.isEmpty(this.getClientType())) {
                this.setClientType(request.getHeader(API_CLIENT_TYPE_HEADER));
            }
            if (StringUtils.isEmpty(this.getClientCode())) {
                this.setClientCode(request.getHeader(API_CLIENT_CODE_HEADER));
            }
        } catch (Exception e) {
            logger.error("参数解析异常", e);
        }
    }

    /**
     * 通过表单请求填充参数。
     */
    public void inflateByFormRequest(HttpServletRequest request) {
        this.setName(request.getParameter(API_NAME));
        this.setParams(request.getParameter(API_PARAMS));
        // Token支持Header和Param方式传值
        this.setToken(request.getParameter(API_TOKEN));
        if (StringUtils.isEmpty(this.getToken())) {
            this.setToken(request.getHeader(API_TOKEN_HEADER));
        }
        // 设备类型及设备唯一标识支持Header和Param方式传值
        this.setClientType(request.getParameter(API_CLIENT_TYPE));
        if (StringUtils.isEmpty(this.getClientType())) {
            this.setClientType(request.getHeader(API_CLIENT_TYPE_HEADER));
        }
        this.setClientCode(request.getParameter(API_CLIENT_CODE));
        if (StringUtils.isEmpty(this.getClientCode())) {
            this.setClientCode(request.getHeader(API_CLIENT_CODE_HEADER));
        }
        this.setTimestamp(request.getParameter(API_TIMESTAMP));
        this.setSign(request.getParameter(API_SIGN));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "ApiParam{" +
                "name='" + name + '\'' +
                ", params='" + params + '\'' +
                ", token='" + token + '\'' +
                ", clientType='" + clientType + '\'' +
                ", clientCode='" + clientCode + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
