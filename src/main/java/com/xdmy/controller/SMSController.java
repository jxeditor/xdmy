package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author xz
 * @Date 2022/9/13 09:38
 * @Description TODO
 */
@RestController
@RequestMapping("/sms")
public class SMSController extends BaseController {
    @RequestMapping("/sendSurplusStock")
    public String sendSurplusStock(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject result = serviceFacade.getStockService().findSurplusStock();
        return new JSONReturn(result).toString();
    }
}