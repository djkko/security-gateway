package cn.denvie.api.service;

import cn.denvie.api.gateway.common.EnctyptType;
import cn.denvie.api.gateway.common.TokenParam;
import cn.denvie.api.gateway.core.ApiConfig;
import cn.denvie.api.gateway.core.ApiToken;
import cn.denvie.api.gateway.core.TokenService;
import cn.denvie.api.gateway.utils.RSAUtils;
import cn.denvie.api.gateway.utils.RandomUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenServiceImpl implements TokenService {

    private static volatile ConcurrentHashMap<String, ApiToken> sTokenMap;
    private static volatile ConcurrentHashMap<String, String> sUserTokenMap;

    static {
        sTokenMap = new ConcurrentHashMap<>();
        sUserTokenMap = new ConcurrentHashMap<>();
    }

    @Override
    public ApiToken createToken(TokenParam param) {
        if (param == null) return null;

        String token = sUserTokenMap.get(param.getMemberId());
        ApiToken apiToken = StringUtils.isEmpty(token) ? null : sTokenMap.get(token);
        if (apiToken == null) {
            apiToken = new ApiToken();
            apiToken.setId(System.currentTimeMillis());
            apiToken.setMemberId(param.getMemberId());
            apiToken.setAccessToken(RandomUtils.generateUuid());

            sTokenMap.put(apiToken.getAccessToken(), apiToken);
            sUserTokenMap.put(param.getMemberId(), apiToken.getAccessToken());
        }

        // 生成密钥
        if (ApiConfig.ENCTYPT_TYPE == EnctyptType.AES) {
            apiToken.setSecret(RandomUtils.generateSecret());
        } else if (ApiConfig.ENCTYPT_TYPE == EnctyptType.RSA) {
            Map<String, String> keyMap = RSAUtils.generateRSAKeyBase64(512);
            String privateKey = keyMap.get(RSAUtils.KEY_PRIVATE);
            String publicKey = keyMap.get(RSAUtils.KEY_PUBLIC);
            apiToken.setSecret(publicKey);
            apiToken.setPrivateScret(privateKey);
        }

        apiToken.setClientIp(param.getClientIp());
        apiToken.setClientType(param.getClientType());
        apiToken.setClientCode(param.getClientCode());
        apiToken.setClientUserCode(param.getClientUserCode());
        apiToken.setCreateTime(System.currentTimeMillis());
        apiToken.setExpireTime(apiToken.getCreateTime() + ApiConfig.EXPIRE_TIME);

        return apiToken;
    }

    @Override
    public ApiToken getToken(String token) {
        return sTokenMap.get(token);
    }

}
