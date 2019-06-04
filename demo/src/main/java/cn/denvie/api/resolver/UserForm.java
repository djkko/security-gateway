package cn.denvie.api.resolver;

public class UserForm {

    private String userFormId;
    private String userFormName;
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
