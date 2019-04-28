package cn.denvie.api.controller;

import cn.denvie.api.bean.User;
import cn.denvie.api.gateway.common.ApiCode;
import cn.denvie.api.gateway.common.ApiResponse;
import cn.denvie.api.gateway.common.TokenParam;
import cn.denvie.api.gateway.core.ApiToken;
import cn.denvie.api.gateway.service.ResponseService;
import cn.denvie.api.gateway.service.TokenService;
import cn.denvie.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ResponseService responseService;

    @PostMapping("/login")
    public ApiResponse login(String username, String password, String clientType,
                                       String clientCode, HttpServletRequest request) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return responseService.error(ApiCode.LOGIN_ERROR.code(),
                    ApiCode.LOGIN_ERROR.message(), null);
        }

        User user = userService.login(username, password);
        if (user == null) {
            return responseService.error(ApiCode.LOGIN_UNEXIST.code(),
                    ApiCode.LOGIN_UNEXIST.message(), null);
        }

        TokenParam param = new TokenParam();
        param.setMemberId(user.getMemberId());
        param.setClientType(clientType);
        param.setClientCode(clientCode);
        param.setClientIp(request.getRemoteAddr());
        param.setClientUserCode(user.getUsername());

        try {
            ApiToken  apiToken = tokenService.createToken(param);
            // 私钥不发送给客户端
            apiToken.setPrivateScret("");
            return responseService.success(apiToken);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return responseService.error(ApiCode.COMMON_ERROR.code(),
                    "生成密钥失败", null);
        }
    }

}
