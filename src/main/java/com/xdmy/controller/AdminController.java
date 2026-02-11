package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.User;
import com.xdmy.utils.ErrorCode;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @RequestMapping(value = "/verifyLogin")
    public String addStock(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        User user = new User();
        user.setUsername(params.get("username"));
        user.setPassword(params.get("password"));
        JSONObject result = serviceFacade.getAdminService().verifyLogin(user);
        if (result.getJSONArray("data").size() > 0) {
            return new JSONReturn(result).toString();
        } else {
            return new JSONReturn(ErrorCode.VERIFY_ERROR).toString();
        }
    }
}
