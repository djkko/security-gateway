package cn.denvie.api;

import cn.denvie.api.gateway.core.ApiConfig;
import cn.denvie.api.gateway.core.ApiRequest;
import cn.denvie.api.gateway.service.SignatureService;
import cn.denvie.api.gateway.service.SubSignatureService;
import cn.denvie.api.gateway.utils.AESUtils;
import cn.denvie.api.gateway.utils.MD5Utils;
import cn.denvie.api.gateway.utils.RSAUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.Base64Utils;

import java.net.URLEncoder;

public class ApiTest {

    static final String CHARSET = "UTF-8";

    private SignatureService signatureService;
    private SubSignatureService subSignatureService;

    @Before
    public void init() {
        ApiConfig apiConfig = new ApiConfig();
        signatureService = apiConfig.signatureService();
        subSignatureService = apiConfig.subSignatureService();
    }

    @Ignore
    @Test
    public void generateBase64Params() throws Exception {
        System.out.println("========== 生成Base64请求参数 ==========");
        String apiName = "user_add";
        String accessToken = "3343fd1f23544c19a622d1a3dae52fd3";
        String secret = "9babb8b9ec5c033f";
        String params = Base64Utils.encodeToString("{\"username\":\"aaaa\", \"password\":\"aaaa\"}".getBytes());
        String paramsEncode = URLEncoder.encode(params, CHARSET);
        generateParam(apiName, secret, accessToken, params, paramsEncode);
    }

    @Ignore
    @Test
    public void generateAesParams() throws Exception {
        System.out.println("========== 生成AES请求参数 ==========");
        String apiName = "user_add";
        String accessToken = "a7e6ab7c535a4648aeae863fcccee668";
        String secret = "c6c10f2b49b15f07";
        String params = AESUtils.encryptToBase64("{\"username\":\"user111\", \"password\":null, \"userForm\":{\"userFormId\": \"iG0zcxD2\", \"userFormName\":null, \"userFormPassword\":\"fbbb\"}}", secret);
        String paramsEncode = URLEncoder.encode(params, CHARSET);
        generateParam(apiName, secret, accessToken, params, paramsEncode);
    }

    @Ignore
    @Test
    public void generateRsaParams() throws Exception {
        System.out.println("========== 生成RSA请求参数 ==========");
        String apiName = "user_add";
        String accessToken = "71905ddaaa5a414aac1b53eb1eb83008";
        String secret = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKt3GtsOYX8bDOL5mpXHr5ZOd7fMTUOvWt0/CzROgZiF/7qKsMdIzFGY3xwnkj/5JRXoinuN6+yH+H2V3H8K9tkCAwEAAQ==";
        String params = RSAUtils.encryptByPublicKey(secret, "{\"username\":\"bbb\", \"password\":\"ccc\"}");
        String paramsEncode = URLEncoder.encode(params, CHARSET);
        generateParam(apiName, secret, accessToken, params, paramsEncode);
    }

    private void generateParam(String apiName, String secret, String accessToken, String params, String paramsEncode) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setApiName(apiName);
        apiRequest.setSecret(secret);
        apiRequest.setAccessToken(accessToken);
        apiRequest.setParams(params);
        apiRequest.setTimestamp(System.currentTimeMillis() + "");
        String sign = signatureService.sign(apiRequest);

        System.out.println("name：" + apiName);
        System.out.println("params：" + params);
        System.out.println("paramsEncoded：" + paramsEncode);
        System.out.println("token：" + accessToken);
        System.out.println("secret：" + secret);
        System.out.println("clientType：" + "android");
        System.out.println("clientCode：" + "LO290DAL183K");
        System.out.println("timestamp：" + apiRequest.getTimestamp());
        System.out.println("sign：" + sign);
    }

    @Ignore
    @Test
    public void decrypt() throws Exception {
        String key = "2f361dabda73e46f";
        String text = "{\"coinType\":\"TOP\",\"amount\":12.34,\"orderNo\":\"order-00001\",\"pushUrl\":\"http://127.0.0.1/mall/order/confirm\"}";
        String s = AESUtils.encryptToBase64(text, key);
        String result = AESUtils.decryptStringFromBase64("fvu94T0cHtF5ghwSt736GUS11BeTOEqhqu9ezPAPi1H69ygDmzdSKZMwfj6Aw03Ep5AqN32de/Up8iRfwz8KYQ==", key);
        System.out.println(result);
    }

    @Ignore
    @Test
    public void testSign() {
        String apiName = "/Api/User/getCoinList";
        String accessToken = "faee9b97570e4a839c01d38db0a42b9c";
        String secret = "b207314fbbf33bb4";
        String params = "468ia0FzsAVKYAaIQgI4PnEVnfv5Mf/vOahe/AnB0IQNzMI13M4sCjX1R3KLME2t";
        String timestamp = "1560320823";
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(secret)
                .append(apiName)
                .append(params)
                .append(accessToken)
                .append(timestamp)
                .append(secret);
        String sign = keyBuilder.toString();
        System.err.println(sign);
        System.err.println(MD5Utils.md5(sign).toUpperCase());
    }

    @Ignore
    @Test
    public void testSubApi() throws Exception {
        System.out.println("========== 生成AES请求参数 ==========");
        String apiName = "user_list";
        String secret = "safe_api_gateway";
        String params = AESUtils.encryptToBase64("{\"name\":\"user111\"}", secret);
        String time = System.currentTimeMillis() + "";

        ApiRequest request = new ApiRequest();
        request.setApiName(apiName);
        request.setParams(params);
        request.setSecret(secret);
        request.setTimestamp(time);
        String sign = subSignatureService.sign(request);

        System.out.println("name：" + apiName);
        System.out.println("params：" + params);
        System.out.println("secret：" + secret);
        System.out.println("timestamp：" + time);
        System.out.println("sign：" + sign);
    }

}
