package cn.denvie.api.gateway.core;

/**
 * Api请求参数的封装。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class ApiRequest {

    private String apiName;         // 调用的Api名称
    private String memberId;        // 会员ID
    private String accessToken;     // 访问Token
    private String secret;          // 发送给客户端的密钥，如AES的密钥、RSA的公钥
    private String privateScret;    // 私钥
    private String sign;            // 签名
    private String clientIp;        // 客户端IP
    private String clientType;      // 客户端类别，android、ios、web...
    private String clientCode;      // 设备唯一标识
    private String timestamp;       // 客户端请求时间
    private String params;          // 客户端请求参数
    private boolean isLogin;        // 是否已登录

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getPrivateScret() {
        return privateScret;
    }

    public void setPrivateScret(String privateScret) {
        this.privateScret = privateScret;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
