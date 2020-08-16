package cn.denvie.api;

import cn.denvie.api.gateway.client.ApiClientService;
import cn.denvie.api.gateway.client.InvokeParam;
import cn.denvie.api.gateway.common.ApiException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private ApiClientService apiClientService;

    @Ignore
    @Test
    public void testApiClient() throws ApiException {
        InvokeParam.Builder builder = new InvokeParam.Builder("user_list");
        builder.addParam("name", "ApiClientName");
        builder.sign("使用自定义规则生成的签名");
        builder.addHeader("header1", "value1")
                .addHeader("header1", "value2");
        String result = apiClientService.post(builder);
        System.out.println(result);
    }

    @Ignore
    @Test
    public void testApiClient2() throws ApiException {
        InvokeParam.Builder builder = new InvokeParam.Builder("user_list")
                .baseUrl("http://192.168.8.18:8090/subApi")
                .secret("safe_api_gateway");
        builder.addParam("name", "ApiClientName");
        String result = apiClientService.post(builder);
        System.out.println(result);
    }

}
