# api-gateway

#### 介绍
一款简单、安全、灵活的API网关框架，可替代传统的Controller层，提升接口开发效率、同时保证接口安全。

#### @ApiMapping用法
```
@Service
public class UserServiceImpl implements UserService {
    @ApiMapping(value = "user_add", needLogin = true, needParams = true)
    public User add(String username, String password) throws ApiException {
        return new User();
    }
}
```

#### 配置项
```
## 加密方式
cn.denvie.api.enctyptType=rsa
## 是否启用客户端与服务端时间差校验
cn.denvie.api.ckeckTimestamp=true
## 允许的客户端请求时间与服务端时间差
cn.denvie.api.timestampDiffer=900000
## 是否启用客户端设备校验
cn.denvie.api.checkDevice=true
## Token的有效期（毫秒）
cn.denvie.api.tokenValidTime=1209600000
```

#### 自定义Token管理TokenService的实现
```
import cn.denvie.api.gateway.common.TokenParam;
import cn.denvie.api.gateway.core.ApiToken;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    @Override
    public ApiToken createToken(TokenParam param) throws NoSuchAlgorithmException {
        return null;
    }

    @Override
    public ApiToken getToken(String token) {
        return null;
    }
}
```

#### 自定义接口调用结果ResponseService的实现
```
import cn.denvie.api.gateway.common.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public class ResponseServiceImpl implements ResponseService {
    @Override
    public ApiResponse success(Object data) {
        return null;
    }

    @Override
    public ApiResponse success(String code, String message, Object data) {
        return null;
    }

    @Override
    public ApiResponse error(String code, String message, Object data) {
        return null;
    }
}
```

#### 自定义签名生成规则SignatureService的实现
```
import cn.denvie.api.gateway.core.ApiRequest;
import org.springframework.stereotype.Service;

@Service
public class SignatureServiceImpl implements SignatureService {
    @Override
    public String sign(ApiRequest param) {
        return null;
    }
}
```