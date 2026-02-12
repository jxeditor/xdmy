package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.User;

public interface IAdminService {
    JSONObject verifyLogin(User user);
    User getUserByToken(String token);
}
