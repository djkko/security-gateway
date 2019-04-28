package cn.denvie.api.gateway.common;

import cn.denvie.api.gateway.utils.AESUtils;
import cn.denvie.api.gateway.utils.MD5Utils;
import cn.denvie.api.gateway.utils.RSAUtils;

import java.net.URLEncoder;

/**
 * 统一的Api请求参数格式。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class ApiParam {

    public static final String API_NAME = "name";
    public static final String API_PARAMS = "params";
    public static final String API_TOKEN = "token";
    public static final String API_CLIENT_TYPE = "clientType";
    public static final String API_CLIENT_CODE = "clientCode";
    public static final String API_TIMESTAMP = "timestamp";
    public static final String API_SIGN = "sign";

    public static void main(String[] args) throws Exception {
        String apiName = "user_add";
//        String apiName = "user_list";
        String accessToken = "0faef00284ed4439963e77c4ddda52f2";
        String secret = "d4c0cf60e0173291";
//        String params = Base64Utils.encodeToString("{\"username\":\"aaa\", \"password\":\"aaa\"}".getBytes());
//        String params = Base64Utils.encodeToString("{}".getBytes());
        String params = AESUtils.encryptToBase64("{\"username\":\"bbb\", \"password\":\"ccc\"}", secret);
//        String params = RSAUtils.encryptByPublicKey(secret,"{\"username\":\"bbb\", \"password\":\"ccc\"}");
        String paramsEncode = URLEncoder.encode(params, "utf-8");

        String timestamp = System.currentTimeMillis() + "";
        String key = secret + apiName + params + accessToken + timestamp + secret;
        String sign = MD5Utils.md5(key).toUpperCase();

        System.err.println("name：" + apiName);
        System.err.println("params：" + params);
        System.err.println("paramsEncode：" + paramsEncode);
        System.err.println("token：" + accessToken);
        System.err.println("secret：" + secret);
        System.err.println("clientType：" + "android");
        System.err.println("clientCode：" + "LO290DAL183K");
        System.err.println("timestamp：" + timestamp);
        System.err.println("key：" + key);
        System.err.println("sign：" + sign);
    }

}
