package com.xdmy.service.inter.impl;


import com.xdmy.datasource.DBContextHolder;
import com.xdmy.domain.Stock;
import com.xdmy.domain.User;
import com.xdmy.service.inter.IAdminService;
import com.xdmy.service.inter.IStockService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
                    JSONObject obj = new JSONObject();
                    obj.put("id", user.getId());
                    obj.put("username", user.getUsername());
                    obj.put("password", user.getPassword());
                    obj.put("role", user.getRole());
                    data.put(obj);
                }
            }
            result.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
