package cn.denvie.api.service;

import cn.denvie.api.bean.User;

/**
 * 用户服务。
 */
public interface UserService {

    User login(String userId, String username, String password);

}
