package cn.denvie.api;

import cn.denvie.api.gateway.core.ApiConfig;
import cn.denvie.api.gateway.core.ApiRequest;
import cn.denvie.api.gateway.service.SignatureService;
import cn.denvie.api.gateway.utils.AESUtils;
import cn.denvie.api.gateway.utils.RSAUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.Base64Utils;

import java.net.URLEncoder;

public class ApiTest {

    static final String CHARSET = "UTF-8";

    private SignatureService signatureService;

    @Before
    public void init() {
        signatureService = new ApiConfig().signatureService();
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
        String accessToken = "d7ce0859e7954c91b448e8930e77fb8b";
        String secret = "20188ac8c27aa644";
        String params = AESUtils.encryptToBase64("{\"username\":\"bbbb\", \"password\":\"bbbb\"}", secret);
        String paramsEncode = URLEncoder.encode(params, CHARSET);
        generateParam(apiName, secret, accessToken, params, paramsEncode);
    }

    @Ignore
    @Test
    public void generateRsaParams() throws Exception {
        System.out.println("========== 生成RSA请求参数 ==========");
        String apiName = "user_add";
        String accessToken = "3343fd1f23544c19a622d1a3dae52fd3";
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
        String key = "76833c9a79718a56";
        String text = "{\"coinType\":\"TOP\",\"amount\":12.34,\"orderNo\":\"order-00001\",\"pushUrl\":\"http://127.0.0.1/mall/order/confirm\"}";
        String s = AESUtils.encryptToBase64(text, key);
        String result = AESUtils.decryptStringFromBase64("YCBXw0J4V/pyNaBUj7ctjG6G+pyO0lcWUK6Cbgb99wZI62Zjq//Tmd/Qsf+PxNWlEuGDJi1mWRoX" +
                "Oql63pl32JUZTo/qQRWx+KwQwLaUjzUwwdEQRuQFLQDqv0q8dMmFUH7Cw5NGz1mcFc2zOLC6yw==", key);
        System.out.println(result);
    }

}
