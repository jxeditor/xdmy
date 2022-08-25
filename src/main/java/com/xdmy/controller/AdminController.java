package com.xdmy.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.User;
import com.xdmy.utils.ErrorCode;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
