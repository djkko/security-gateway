package cn.denvie.api.gateway.common;

/**
 * 生成Token的参数。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class TokenParam {

    private String memberId;        // 会员ID
    private String clientIp;        // 客户端IP
    private String clientType;      // 客户端类别，android、ios、web...
    private String clientCode;      // 设备标识
    private String clientUserCode;  // 设备用户标识

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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

    public String getClientUserCode() {
        return clientUserCode;
    }

    public void setClientUserCode(String clientUserCode) {
        this.clientUserCode = clientUserCode;
    }
}
