package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/screen")
public class ScreenController extends BaseController {

    @RequestMapping("/getShipment1ChartData")
    public String getShipment1ChartData(@RequestParam(value = "customerName", defaultValue = "") String customerName,
                                        @RequestParam(value = "bizStartDate", defaultValue = "undefined") String bizStartDate,
                                        @RequestParam(value = "bizEndDate", defaultValue = "undefined") String bizEndDate,
                                        HttpServletRequest request
    ) {
        JSONObject result = serviceFacade.getScreenService().getShipment1ChartData(customerName, bizStartDate, bizEndDate, getCompanyName(request));
        return new JSONReturn(result).toString();
    }


    @RequestMapping("/getShipment2ChartData")
    public String getShipment2ChartData(HttpServletRequest request) {
        JSONObject result = serviceFacade.getScreenService().getShipment2ChartData(getCompanyName(request));
        return new JSONReturn(result).toString();
    }

}
