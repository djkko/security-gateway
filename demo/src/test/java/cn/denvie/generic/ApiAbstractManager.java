package cn.denvie.generic;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ApiAbstractManager extends AbstractManager {

    public void requestApi(String data, Callback<Result<User>> callback) {
        invoke(data, callback, User.class);
    }

    public void requestApiList(String data, Callback<Result<List<User>>> callback) {
        invokeList(data, callback, User.class);
    }

    public static void main(String[] args) {
        // 构建数据
        Random random = new Random();
        Gson gson = new Gson();
        List<User> users = new ArrayList<>();
        User user;
        for (int i = 0; i <= 5; i++) {
            user = new User();
            user.setName("name" + i);
            user.setAge(20 + i);
            user.setSex(random.nextInt(2) % 2 == 0 ? "male" : "female");
            users.add(user);
        }

        // 调用API
        ApiAbstractManager apiManager = new ApiAbstractManager();
        // 解析对象
        Result<User> userResult = new Result<>();
        userResult.setData(users.get(0));
        apiManager.requestApi(gson.toJson(userResult), new Callback<Result<User>>() {
            @Override
            public void onDataLoaded(Result<User> result) {
                System.err.println(gson.toJson(result));
            }
        });
        // 解析列表
        Result<List<User>> usersResult = new Result<>();
        usersResult.setData(users);
        apiManager.requestApiList(gson.toJson(usersResult), new Callback<Result<List<User>>>() {
            @Override
            public void onDataLoaded(Result<List<User>> result) {
                System.err.println(gson.toJson(result));
            }
        });
    }

}
