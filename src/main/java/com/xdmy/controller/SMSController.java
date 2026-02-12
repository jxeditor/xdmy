package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.utils.JSONReturn;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author xz
 * @Date 2022/9/13 09:38
 * @Description 短信发送控制器
 */
@RestController
@RequestMapping("/sms")
public class SMSController extends BaseController {
    @RequestMapping("/sendSurplusStock")
    public String sendSurplusStock(@RequestBody java.util.Map<String, Object> request) throws IOException {
        String companyName = (String) request.get("companyName");
        JSONObject result = serviceFacade.getStockService().findSurplusStock(companyName);
        return new JSONReturn(result).toString();
    }
}