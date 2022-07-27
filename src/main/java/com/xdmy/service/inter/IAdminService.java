package com.xdmy.service.inter;

import com.xdmy.domain.Stock;
import com.xdmy.domain.User;
import org.json.JSONObject;

public interface IAdminService {
    JSONObject verifyLogin(User user);
}
