package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Stock;
import com.xdmy.domain.User;

public interface IAdminService {
    JSONObject verifyLogin(User user);
}
