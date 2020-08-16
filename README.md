# security-gateway

#### 介绍
**security-gateway**是一款基于SpringBoot的简单、安全、灵活的API网关框架，可替代传统的Controller层，提升接口开发效率。
同时，支持请求参数的加密加签，保证接口安全。  
* 参数加密方式支持**Base64**、**AES**、**RSA**，可通过配置文件设置
* 签名方式提供了默认实现，调用方也可通过实现**SignatureService**接口的@Service自定义规则
* 兼容HandlerMethodArgumentResolver，可通过自定义**HandlerMethodArgumentResolver**实例注入参数
* 支持方法参数添加@Valid注解进行参数校验  
* 支持自定义**ApiInvokeInterceptor**实现请求接口拦截  
* 提供ApiClientService服务实现多个安全网关服务间互调操作  

#### 用法
(1). 创建数据库和表
```SQL
CREATE DATABASE `security-gateway` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
```
```SQL
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
```Shell
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=none
spring.jpa.database-platform=org.hibernate.dialect.MySQL5Dialect

## data source config, use HikariDataSource
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/security-gateway?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
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

(3). 在用户登录、注册过程中，调用**ApiTokenService**创建一个ApiToken，并将accessToken、secret、expireTime一并返回给客户端。

(4). 在Spring管理的Bean的方法上添加 **@ApiMapping** 注解，即可对外暴露一个接口。
```Java
@Service
public class UserServiceImpl implements UserService {
    static final String BASE_PATH = "/Api/";
    @ApiMapping(value = BASE_PATH + "packet/ownerSend", paramNames = {"form"})
    public CommonResponse ownerSend(@RequestBody PacketSendForm form){
        return packetService.ownerSend(form);
    }
}
```
**@ApiMapping**的属性说明：  
value：接口名，需全局唯一  
needLogin：是否需要Token校验，默认为true   
paramNames：接口参数名列表（建议通过注解设置，防止部分环境下字节码解析获取不到参数名）  

(5). 需要Token校验的Api的接口请求路径为 http://localhost:8080/api  

传参方式为“FORM”时的接口的调用格式为：
```HTML
http://localhost:8080/api?name=user_add&params={"username":"denvie","password":"aa123456"}&token=3343fd1f23544c19a622d1a3dae52fd3&clientType=android&clientCode=LO290DAL183K&timestamp=1556442217873&sign=BE16798DBA1561A8AD369C0438AEE5A0
```    
传参方式为“BODY”时的接口的调用格式为：http://localhost:8080/api，请求BODY的类型为“application/json”，BODY值格式如下：  
```JSON
{
	"name": "/user/add",
	"params": "o+bA2FaNZXJYLmEJKTSmXbj9nnydfUYwYUFkEo/vsOQ1QMkNY9EXeqb2hTv7pns9",
	"token": "d7ce0859e7954c91b448e8930e77fb8b",
	"clientType": "android",
	"clientCode": "LO290DAL183K",
	"timestamp": "1559552986907",
	"sign": "3758874B4E8BB86E9F01634035AE376D"
}
```  
参数说明如下：  
name：@ApiMapping定义的接口名  
params: JSON格式加密后的请求参数  
token：Token值  
clientType：客户端类别，Android、iOS、Web等  
clientCode：客户端设备唯一标识  
timestamp：Long类型的请求时间戳  
sign：参数签名  
其中，token、clientType、clientCode三个参数的传值同时支持Header及Param方式。

(6). 不需要Token校验的SubApi的接口请求路径为 http://localhost:8080/subApi，请求BODY的参数示例：
```JSON
{
    "name": "/user/list",
    "params": "vYqqaGSz6RwQQfLqX18+7omz92Zdplf+HfY1J0uw2uU=",
    "sign": "A3088A8DD9201EDD20A84A606B033D69",
    "timestamp": "1560331044605"
}
``` 
其中，不需要Token校验的接口的@ApiMapping注解必需设置属性needLogin = false！
参数说明如下：  
name：@ApiMapping定义的接口名  
params: JSON格式加密后的请求参数 
timestamp：Long类型的请求时间戳  
sign：参数签名  

(6). SpringBoot启动类添加扫描的API网关包路径声明"cn.denvie.api"：
```Java
@SpringBootApplication(scanBasePackages = {"com.demo", "cn.denvie.api"})
@EntityScan(basePackages = {"com.demo", "cn.denvie.api"})
@EnableJpaRepositories(basePackages = {"com.demo", "cn.denvie.api"})
@ServletComponentScan(basePackages = {"com.demo", "cn.denvie.api"})
public class ToptokenApplication extends SpringBootServletInitializer {

}
```

#### API网关配置项（如不设置，则默认值为以下各项的值）
```Shell
## 参数加密方式，目前支持：Base64、AES、RSA
cn.denvie.api.encryptType=AES
## AES加密算法密钥（16位），若不配置，则自动生成
cn.denvie.api.aesKey=
## RSA加密算法公钥，若不配置，则自动生成
cn.denvie.api.rsaPublicKey=
## RSA加密算法私钥，若不配置，则自动生成
cn.denvie.api.rsaPrivateKey=
## 是否启用客户端与服务端时间差校验
cn.denvie.api.checkTimestamp=false
## 允许的客户端请求时间与服务端时间差
cn.denvie.api.timestampDiffer=600000
## 是否启用客户端设备校验
cn.denvie.api.checkDevice=true
## Token的有效期（毫秒）
cn.denvie.api.tokenValidTime=1209600000
## 多设备登录策略：ALLOW（允许同时登录）、REPLACE（挤掉对方）、REFUSE（拒绝登录）
cn.denvie.api.multiDeviceLogin=REPLACE
## 传参方式：FORM、BODY
cn.denvie.api.paramType=BODY
## 是否开启日志输出
cn.denvie.api.enableLogging=false
## Sub Api 的AES私钥或者RSA公钥
cn.denvie.api.subSecret=safe_api_gateway
## Sub Api 的RSA私钥
cn.denvie.api.subPrivateSecret=
## Rest Client 调用的接口路径
cn.denvie.api.client-base-url=http://192.168.8.18:8090/subApi
## Rest Client 参数加密的私钥
cn.denvie.api.client-secret=safe_api_gateway
## Rest Client 连接超时时间
cn.denvie.api.client-connect-timeout=8000
## Rest Client 读取超时时间
cn.denvie.api.client-read-timeout=30000
## Rest Client 请求编码
cn.denvie.api.client-request-charset=UTF-8
```

#### API安全网关之间互调操作说明
在配置文件中添加**ApiClient**相关的设置  
```Shell
## Rest Client 调用的接口路径
cn.denvie.api.client-base-url=http://192.168.8.18:8080/subApi
## Rest Client 参数加密的私钥
cn.denvie.api.client-secret=safe_api_gateway
## Rest Client 连接超时时间（可选）
cn.denvie.api.client-connect-timeout=8000
## Rest Client 读取超时时间（可选）
cn.denvie.api.client-read-timeout=30000
## Rest Client 请求编码（可选）
cn.denvie.api.client-request-charset=UTF-8
```
在需要调用API的服务类中注入**ApiClientService**  
```
@Autowired
private ApiClientService apiClientService;
```
请求的示例代码如下：  
```
InvokeParam.Builder builder = new InvokeParam.Builder("/api/name");
builder.addParam("name", "ApiClientName");
String result = apiClientService.post(builder);
System.out.println(result);
```
以上配置仅适用于应用内只调用一个外部安全API服务器的情况，如果需要调用多个安全API服务器，则需要通用以下示例方法动态设置请求的服务器地址及使用的加密密钥：  
```
InvokeParam.Builder builder = new InvokeParam.Builder("/api/name ")
.baseUrl("http://host.serverA:8080/subApi")
.secret("safe_apigateway1");
builder.addParam("name", "ApiClientName");
String result = apiClientService.post(builder);
System.out.println(result);

InvokeParam.Builder builder2 = new InvokeParam.Builder("/api/name ")
.baseUrl("http://host.serverB:8080/subApi")
.secret("safe_apigateway2");
Builder2.addParam("name", "ApiClientName");
String result2 = apiClientService.post(builder);
System.out.println(result2);
```
构建InvokeParam.Builder时也可以动态设置签名及添加Header：
```
InvokeParam.Builder builder = new InvokeParam.Builder("user_list");
builder.addParam("name", "ApiClientName");
builder.sign("使用自定义规则生成的签名");
builder.addHeader("header1", "value1")
    .addHeader("header1", "value2");
```

#### 自定义接口调用结果**ResponseService**的实现
```Java
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

#### 自定义签名生成规则**SignatureService**的实现
```Java
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

#### 自定义Sub Api签名生成规则**SubSignatureService**的实现
```Java
import cn.denvie.api.gateway.core.ApiRequest;
import org.springframework.stereotype.Service;

@Service
public class SubSignatureServiceImpl implements SubSignatureService {
    @Override
    public String sign(ApiRequest param) {
        return null;
    }
}
```
默认签名规则：MD5（secret + apiName + params + secret）.toUpperCase()

#### 自定义接口调用异常处理器**InvokeExceptionHandler**的实现
```Java
import cn.denvie.api.gateway.common.ApiResponse;
import cn.denvie.api.gateway.core.ApiRequest;
import org.springframework.stereotype.Service;

@Service
public class InvokeExceptionHandlerImpl implements InvokeExceptionHandler {
    @Override
    public ApiResponse handle(ApiRequest apiRequest, Throwable e) {
        return null;
    }
}
```

#### 自定义接口请求拦截器**ApiInvokeInterceptor**的实现
```Java
import cn.denvie.api.gateway.common.ApiInvokeInterceptor;
import cn.denvie.api.gateway.common.InvokeCode;
import cn.denvie.api.gateway.core.ApiRequest;

public class ApiInvokeInterceptorImpl implements ApiInvokeInterceptor {
    @Override
    public InvokeCode before(ApiRequest request, Object[] args) {
        // 如果返回的InvokeCode不为空，则中断接口的调用
        return null;
    }

    @Override
    public void error(ApiRequest request, Throwable t) {

    }

    @Override
    public void after(ApiRequest request, Object result) {

    }
}
```