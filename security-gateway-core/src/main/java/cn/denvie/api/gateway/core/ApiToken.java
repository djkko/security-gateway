package cn.denvie.api.gateway.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * ApiToken实体Bean。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
@Entity
@Table(indexes = {@Index(columnList = "accessToken")},
        uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "clientType", "clientCode"})})
public class ApiToken implements Serializable {

    public static final String REQ_PARAM_TOKEN = "req_param_token";
    public static final String REQ_PARAM_TOKEN_OBJ = "req_param_token_obj";

    private static final long serialVersionUID = 5223827895858062476L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;                // 表主键，自增长
    @Column(length = 32, nullable = false)
    private String userId;          // 用户Id
    @Column(length = 128, nullable = false)
    private String userName;        // 用户名
    @Column(length = 64, nullable = false, unique = true)
    private String accessToken;     // 访问Token
    @Column(length = 256)
    private String secret;          // 发送给客户端的密钥，如AES的密钥、RSA的公钥
    @Column(length = 1024)
    private String privateSecret;   // 私钥
    @Column(length = 32)
    private String clientIp;        // 客户端IP
    @Column(length = 32, nullable = false)
    private String clientType;      // 客户端类别，android、ios、web...
    @Column(length = 128, nullable = false)
    private String clientCode;      // 设备唯一标识
    @Column
    private long createTime;        // 创建时间
    @Column
    private long expireTime;        // 失效时间
    @Column(length = 128)
    private String ext1;            // 扩展参数1
    @Column(length = 128)
    private String ext2;            // 扩展参数2

    /**
     * 判断Token是否已过期。
     */
    @Transient
    @JsonIgnore
    public boolean isExpired() {
        return new Date(expireTime).before(new Date());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPrivateSecret() {
        return privateSecret;
    }

    public void setPrivateSecret(String privateSecret) {
        this.privateSecret = privateSecret;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }
}
