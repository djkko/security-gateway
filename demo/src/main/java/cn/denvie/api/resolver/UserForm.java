package cn.denvie.api.resolver;

import javax.validation.constraints.NotNull;

public class UserForm {

    private String userFormId;

    @NotNull(message = "用户名不能为空")
    private String userFormName;

    @NotNull(message = "密码不能为空")
    private String userFormPassword;

    public String getUserFormId() {
        return userFormId;
    }

    public void setUserFormId(String userFormId) {
        this.userFormId = userFormId;
    }

    public String getUserFormName() {
        return userFormName;
    }

    public void setUserFormName(String userFormName) {
        this.userFormName = userFormName;
    }

    public String getUserFormPassword() {
        return userFormPassword;
    }

    public void setUserFormPassword(String userFormPassword) {
        this.userFormPassword = userFormPassword;
    }

    @Override
    public String toString() {
        return "UserForm{" +
                "userFormId='" + userFormId + '\'' +
                ", userFormName='" + userFormName + '\'' +
                ", userFormPassword='" + userFormPassword + '\'' +
                '}';
    }

}
