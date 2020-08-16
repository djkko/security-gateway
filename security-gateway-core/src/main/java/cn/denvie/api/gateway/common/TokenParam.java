package cn.denvie.api.gateway.common;

/**
 * 生成Token的参数。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class TokenParam {

    /**
     * 用户Id，不能为空
     */
    private String userId;
    /**
     * 用户名，不能为空
     */
    private String userName;
    /**
     * 客户端IP
     */
    private String clientIp;
    /**
     * 客户端类别，不能为空，android、ios、web...
     */
    private String clientType;
    /**
     * 设备标识，不能为空
     */
    private String clientCode;
    /**
     * 扩展参数1
     */
    private String ext1;
    /**
     * 扩展参数2
     */
    private String ext2;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    @Override
    public String toString() {
        return "TokenParam{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", clientIp='" + clientIp + '\'' +
                ", clientType='" + clientType + '\'' +
                ", clientCode='" + clientCode + '\'' +
                ", ext1='" + ext1 + '\'' +
                ", ext2='" + ext2 + '\'' +
                '}';
    }
}
