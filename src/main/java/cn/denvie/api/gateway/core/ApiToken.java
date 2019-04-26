package cn.denvie.api.gateway.core;

import java.io.Serializable;

/**
 * ApiToken实体Bean。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class ApiToken implements Serializable {

    private long id;                // 表主键，自增长
    private String memberId;        // 会员ID
    private String accessToken;     // 访问Token
    private String secret;          // 发送给客户端的密钥
    private String clientIp;        // 客户端IP
    private String clientType;      // 客户端类别，android、ios、web...
    private String clientCode;      // 设备标识
    private String clientUserCode;  // 设备用户标识
    private long createTime;       // 创建时间
    private long expireTime;       // 失效时间

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
