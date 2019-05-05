package cn.denvie.api.service;

import cn.denvie.api.bean.User;
import cn.denvie.api.gateway.common.ApiException;
import cn.denvie.api.gateway.core.ApiMapping;
import cn.denvie.api.gateway.core.ApiRequest;
import cn.denvie.api.gateway.utils.RandomUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {

    private static volatile ConcurrentHashMap<String, User> sUserMap;

    static {
        sUserMap = new ConcurrentHashMap<>();
    }

    @Override
    public User login(String userId, String username, String password) {
        User user = sUserMap.get(username);
        if (user == null) {
            user = new User();
            String tempId = StringUtils.isEmpty(userId) ? RandomUtils.generateShortUuid() : userId;
            user.setMemberId(tempId);
            user.setUsername(username);
            user.setPassword(password);
            sUserMap.put(username, user);
        }
        return user;
    }

    @ApiMapping(value = "user_add")
    public User add(String username, String password) throws ApiException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new ApiException("用户名和密码不能为空");
        }

        if (sUserMap.get(username) != null) {
            throw new ApiException("用户[" + username + "]已存在");
        }

        User user = new User();
        user.setMemberId(RandomUtils.generateShortUuid());
        user.setUsername(username);
        user.setPassword(password);
        sUserMap.put(user.getUsername(), user);

        return user;
    }

    @ApiMapping(value = "user_list")
    public List<User> list(HttpServletRequest request, ApiRequest apiRequest) {
        System.out.println("RemoteAddr: " + request.getRemoteAddr());
        System.out.println("MemberId(: " + apiRequest.getMemberId());
        return new ArrayList<>(sUserMap.values());
    }

}
