# api-gateway

#### 介绍
一款基于SpringBoot的简单、安全、灵活的API网关框架，可替代传统的Controller层，提升接口开发效率。
同时，支持请求参数的加密加签，保证接口安全。  
* 参数加密方式支持Base64、AES、RSA，可通过配置文件设置
* 签名方式提供了默认实现，调用方也可通过实现SignatureService接口的@Service自定义规则
* 兼容HandlerMethodArgumentResolver，可通过自定义HandlerMethodArgumentResolver实例注入参数。

#### 用法
(1). 创建数据库和表
```
CREATE DATABASE `api-gateway` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
```
```
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for api_token
-- ----------------------------
DROP TABLE IF EXISTS `api_token`;
CREATE TABLE `api_token`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `access_token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `client_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `client_ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `client_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` bigint(20) NULL DEFAULT NULL,
  `expire_time` bigint(20) NULL DEFAULT NULL,
  `ext1` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ext2` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `private_secret` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `secret` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKepcpf927l9olsm9c9ky6anydp`(`user_id`, `client_type`, `client_code`) USING BTREE,
  UNIQUE INDEX `UK_gk8n48qmwccac74paobkchmka`(`access_token`) USING BTREE,
  INDEX `IDXinllpovnunrvf1xbvm6q34t3j`(`access_token`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```

(2). 在配置文件中配置数据源
```
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=none
spring.jpa.database-platform=org.hibernate.dialect.MySQL5Dialect

## data source config, use HikariDataSource
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/api-gateway?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=50
spring.datasource.hikari.auto-commit=true
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=300000
spring.datasource.hikari.max-lifetime=18000000
spring.datasource.hikari.pool-name=HikariCp4HikeApi
spring.datasource.hikari.connection-test-query=SELECT 1
```

(3). 在用户登录、注册过程中，调用ApiTokenService创建一个ApiToken，并将accessToken、secret、expireTime一并返回给客户端。

(4). 在Spring管理的Bean的方法上添加@ApiMapping注解，即可对外暴露一个接口。
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
needParams：参数是否做判空校验  

传参方式为“FORM”时的接口的调用格式为：  
http://localhost:8080/api?name=user_add&params={"username":"denvie","password":"aa123456"}&token=3343fd1f23544c19a622d1a3dae52fd3&clientType=android&clientCode=LO290DAL183K&timestamp=1556442217873&sign=BE16798DBA1561A8AD369C0438AEE5A0  
传参方式为“BODY”时的接口的调用格式为：http://localhost:8080/api，请求BODY的类型为“application/json”，BODY值格式如下：  
{
	"name": "user_add",
	"params": "o+bA2FaNZXJYLmEJKTSmXbj9nnydfUYwYUFkEo/vsOQ1QMkNY9EXeqb2hTv7pns9",
	"token": "d7ce0859e7954c91b448e8930e77fb8b",
	"clientType": "android",
	"clientCode": "LO290DAL183K",
	"timestamp": "1559552986907",
	"sign": "3758874B4E8BB86E9F01634035AE376D"
}  

参数说明如下：  
name：@ApiMapping定义的接口名  
params: JSON格式的请求参数  
token：Token值  
clientType：客户端类别，android、ios、web等  
clientCode：客户端设备唯一标识  
timestamp：Long类型的请求时间戳  
sign：参数签名  
其中，传参方式为“FORM”时token、clientType、clientCode三个参数的传值同时支持Header及Param方式，Header中取不到时，会从Param中再取一次。

#### API网关配置项（如不设置，则默认值为以下各项的值）
```
## 参数加密方式，目前支持：Base64、AES、RSA
cn.denvie.api.enctyptType=AES
## AES加密算法密钥（16位），若不配置，则自动生成
cn.denvie.api.aesKey=
## RSA加密算法公钥，若不配置，则自动生成
cn.denvie.api.rsaPublicKey=
## RSA加密算法私钥，若不配置，则自动生成
cn.denvie.api.rsaPrivateKey=
## 是否启用客户端与服务端时间差校验
cn.denvie.api.ckeckTimestamp=true
## 允许的客户端请求时间与服务端时间差
cn.denvie.api.timestampDiffer=900000
## 是否启用客户端设备校验
cn.denvie.api.checkDevice=true
## Token的有效期（毫秒）
cn.denvie.api.tokenValidTime=1209600000
## 多设备登录策略：ALLOW（允许同时登录）、REPLACE（挤掉对方）、REFUSE（拒绝登录）
cn.denvie.api.multiDeviceLogin=ALLOW
## 传参方式：FORM、BODY，默认为：BODY
cn.denvie.api.paramType=BODY
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
默认签名规则：MD5（secret + apiName + token + params + timestamp + secret）.toUpperCase()

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