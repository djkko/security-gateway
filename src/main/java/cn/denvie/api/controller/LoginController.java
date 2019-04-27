package cn.denvie.api.controller;

import cn.denvie.api.bean.User;
import cn.denvie.api.gateway.common.ApiCode;
import cn.denvie.api.gateway.common.ApiResponse;
import cn.denvie.api.gateway.common.TokenParam;
import cn.denvie.api.gateway.core.ApiToken;
import cn.denvie.api.gateway.core.TokenService;
import cn.denvie.api.gateway.utils.RandomUtils;
import cn.denvie.api.gateway.utils.ResponseUtils;
import cn.denvie.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ApiResponse<ApiToken> login(String username, String password, String clientType,
                                       String clientCode, HttpServletRequest request) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResponseUtils.error(ApiCode.LOGIN_ERROR, null);
        }

        User user = userService.login(username, password);
        if (user == null) {
            return ResponseUtils.error(ApiCode.LOGIN_UNEXIST, null);
        }

        TokenParam param = new TokenParam();
        param.setMemberId(user.getMemberId());
        param.setClientType(clientType);
        param.setClientCode(clientCode);
        param.setClientIp(request.getRemoteAddr());
        param.setClientUserCode(user.getUsername());

        ApiToken apiToken = tokenService.createToken(param);
        // 私钥不发送给客户端
        apiToken.setPrivateScret("");

        return ResponseUtils.success(apiToken);
    }

}
