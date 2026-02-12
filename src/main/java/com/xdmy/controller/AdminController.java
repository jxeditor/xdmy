package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.User;
import com.xdmy.utils.ErrorCode;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @RequestMapping(value = "/verifyLogin", consumes = "application/json")
    public String addStock(@RequestBody User user) {
        try {
            // 对密码进行Base64解码
            String encodedPassword = user.getPassword();
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(encodedPassword);
            String decodedPassword = new String(decodedBytes);
            user.setPassword(decodedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject result = serviceFacade.getAdminService().verifyLogin(user);
        if (result.getJSONArray("data").size() > 0) {
            return new JSONReturn(result).toString();
        } else {
            return new JSONReturn(ErrorCode.VERIFY_ERROR).toString();
        }
    }
}
