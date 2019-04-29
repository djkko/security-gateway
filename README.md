# api-gateway

#### 介绍
一款简单、安全、灵活的API网关框架，可替代传统的Controller层，提升接口开发效率。
同时，支持请求参数的加密加签，保证接口安全。
目前，参数加密方式支持Base64、AES、RSA，可通过配置文件设置；签名方式提供了默认实现，
调用方也可通过实现SignatureService接口的@Service自定义规则。

#### 用法
在Spring管理的Bean的方法上添加@ApiMapping注解，即可对外暴露一个接口。
```
@Service
public class UserServiceImpl implements UserService {
    @ApiMapping(value = "user_add", needLogin = true, needParams = true)
    public User add(String username, String password) throws ApiException {
        return new User();
    }
}
```
@ApiMapping的属性说明：  
value：接口名，需全局唯一  
needLogin：是否需要登录鉴权  
needParams：接口是否有参数  

接口的调用格式为：  
http://localhost:8080/api?name=user_add&params={"username":"denvie","password":"aa123456"}&token=3343fd1f23544c19a622d1a3dae52fd3&clientType=android&clientCode=LO290DAL183K&timestamp=1556442217873&sign=BE16798DBA1561A8AD369C0438AEE5A0  

参数说明如下：  
name：@ApiMapping定义的接口名  
params: JSON格式的请求参数  
token：Token值  
clientType：客户端类别，android、ios、web等  
clientCode：客户端设备唯一标识  
timestamp：Long类型的请求时间戳  
sign：参数签名  
其中，token、clientType、clientCode三个参数的传值同时支持Header及Param方式，Header中取不到时，会从Param中再取一次。

#### API网关配置项（如不设置，则默认值为以下各项的值）
```
## 参数加密方式，目前支持：Base64、AES、RSA
cn.denvie.api.enctyptType=AES
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

#### 自定义接口调用异常处理器InvokExceptionHandler的实现
```
import cn.denvie.api.gateway.common.ApiResponse;
import cn.denvie.api.gateway.core.ApiRequest;
import org.springframework.stereotype.Service;

@Service
public class InvokExceptionHandlerImpl implements InvokExceptionHandler {
    @Override
    public ApiResponse handle(ApiRequest apiRequest, Throwable e) {
        return null;
    }
}
```