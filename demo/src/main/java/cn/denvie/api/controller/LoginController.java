package cn.denvie.api.controller;

import cn.denvie.api.bean.User;
import cn.denvie.api.gateway.common.ApiException;
import cn.denvie.api.gateway.common.ApiResponse;
import cn.denvie.api.gateway.common.TokenParam;
import cn.denvie.api.gateway.core.ApiRequest;
import cn.denvie.api.gateway.core.ApiToken;
import cn.denvie.api.gateway.service.ApiTokenService;
import cn.denvie.api.gateway.service.ResponseService;
import cn.denvie.api.resolver.UserFormAnnotation;
import cn.denvie.api.resolver.UserForm;
import cn.denvie.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private ApiTokenService apiTokenService;
    @Autowired
    private ResponseService responseService;

    @PostMapping("/login")
    @ResponseBody
    public ApiResponse login(String userId, String username, String password, String clientType,
                             String clientCode, @UserFormAnnotation UserForm form, HttpServletRequest request, ApiRequest apiRequest) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return responseService.error("110000","用户名或密码错误", null);
        }

        User user = userService.login(userId, username, password);
        if (user == null) {
            return responseService.error("110001","用户不存在", null);
        }

        TokenParam param = new TokenParam();
        param.setUserId(user.getMemberId());
        param.setClientType(clientType);
        param.setClientCode(clientCode);
        param.setClientIp(request.getRemoteAddr());
        param.setUserName(user.getUsername());
        param.setExt1("ext1");
        param.setExt2("ext2");

        try {
            ApiToken  apiToken = apiTokenService.createToken(param);
            // 私钥不发送给客户端
            apiToken.setPrivateSecret("");
            return responseService.success(apiToken);
        } catch (ApiException e) {
            return responseService.error(e.getCode(),e.getDesc(), null);
        }
    }

}
