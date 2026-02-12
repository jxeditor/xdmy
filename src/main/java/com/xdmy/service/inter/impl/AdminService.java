package com.xdmy.service.inter.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xdmy.datasource.DBContextHolder;
import com.xdmy.domain.User;
import com.xdmy.service.inter.IAdminService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService extends BaseService implements IAdminService {
    @Override
    public JSONObject verifyLogin(User user) {
        DBContextHolder.setDbType("primary");
        List<User> userList = daoFacade.getAdminDao().verifyLogin(user);
        return toJSONObject(userList);
    }

    public JSONObject toJSONObject(List<User> userList) {
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        try {
            if (userList != null) {
                for (User user : userList) {
                    // 生成Token
                    String token = generateToken(user.getId(), user.getUsername());
                    // 更新Token到数据库
                    daoFacade.getAdminDao().updateToken(user.getId(), token);
                    // 设置用户的Token
                    user.setToken(token);
                    
                    JSONObject obj = new JSONObject();
                    obj.put("id", user.getId());
                    obj.put("username", user.getUsername());
                    obj.put("role", user.getRole());
                    obj.put("companyName", user.getCompanyName());
                    obj.put("token", token);
                    data.add(obj);
                }
            }
            result.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 生成Token
     * @param userId 用户ID
     * @param username 用户名
     * @return 生成的Token
     */
    private String generateToken(int userId, String username) {
        // 使用UUID生成Token，实际项目中可以使用JWT等更安全的方式
        String token = java.util.UUID.randomUUID().toString() + "-" + userId + "-" + username;
        return token;
    }
    
    @Override
    public User getUserByToken(String token) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getAdminDao().getUserByToken(token);
    }
}
