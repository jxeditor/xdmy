package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Incoming;
import com.xdmy.utils.ErrorCode;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/incoming")
public class IncomingController extends BaseController {

    @RequestMapping("/findAllIncoming")
    public String findAllIncoming(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                  @RequestParam(value = "producerName", defaultValue = "") String producerName,
                                  @RequestParam(value = "productName", defaultValue = "") String productName,
                                  @RequestParam(value = "bizStartDate", defaultValue = "undefined") String bizStartDate,
                                  @RequestParam(value = "bizEndDate", defaultValue = "undefined") String bizEndDate
    ) {
        JSONObject result = serviceFacade.getIncomingService().findAllIncoming(pageNum, pageSize, producerName, productName, bizStartDate, bizEndDate);
        return new JSONReturn(result).toString();
    }

    @RequestMapping(value = "/addIncoming")
    public String addIncoming(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        Incoming incoming = new Incoming();
        incoming.setOdd(params.get("odd"));
        incoming.setProducer(params.get("producer"));
        incoming.setProduct(params.get("product"));
        incoming.setBilldate(params.get("billdate"));
        incoming.setAmount(Integer.parseInt(params.get("amount")));
        incoming.setUnitprice(Double.parseDouble(params.get("unitprice")));
        incoming.setMoney(Integer.parseInt(params.get("amount")) * Double.parseDouble(params.get("unitprice")));
        incoming.setPaystatus(params.get("paystatus"));
        incoming.setRemark(params.get("remark"));
        int result = serviceFacade.getIncomingService().addIncoming(incoming);
        if (result > 0) {
            return new JSONReturn("success", "插入成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.INSERT_FAILED).toString();
        }
    }

    @RequestMapping(value = "/updateIncoming")
    public String updateIncoming(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        Incoming incoming = new Incoming();
        incoming.setId(Integer.parseInt(params.get("id")));
        incoming.setOdd(params.get("odd"));
        incoming.setProducer(params.get("producer"));
        incoming.setProduct(params.get("product"));
        incoming.setBilldate(params.get("billdate"));
        incoming.setAmount(Integer.parseInt(params.get("amount")));
        incoming.setUnitprice(Double.parseDouble(params.get("unitprice")));
        incoming.setMoney(Integer.parseInt(params.get("amount")) * Double.parseDouble(params.get("unitprice")));
        incoming.setPaystatus(params.get("paystatus"));
        incoming.setRemark(params.get("remark"));
        int result = serviceFacade.getIncomingService().updateIncoming(incoming);
        if (result > 0) {
            return new JSONReturn("success", "更新成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.UPDATE_FAILED).toString();
        }
    }

    @RequestMapping("/deleteIncomingById")
    public String deleteIncomingById(@RequestParam(value = "id", defaultValue = "-1") Integer id) {
        int result = serviceFacade.getIncomingService().deleteIncomingById(id);
        if (result > 0) {
            return new JSONReturn("success", "删除成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.DELETE_FAILED).toString();
        }
    }

    @RequestMapping("/updatePaystatusIncomingById")
    public String updatePaystatusIncomingById(@RequestParam(value = "id", defaultValue = "-1") Integer id) {
        int result = serviceFacade.getIncomingService().updatePaystatusIncomingById(id);
        if (result > 0) {
            return new JSONReturn("success", "更新成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.UPDATE_FAILED).toString();
        }
    }

    @RequestMapping("/findProducerNamesByPrefix")
    public String findProducerNamesByPrefix(@RequestBody java.util.Map<String, Object> request) {
        String prefix = (String) request.getOrDefault("prefix", "");
        int pageNum = request.containsKey("pageNum") ? Integer.parseInt(request.get("pageNum").toString()) : 1;
        int pageSize = request.containsKey("pageSize") ? Integer.parseInt(request.get("pageSize").toString()) : 10;
        JSONObject result = serviceFacade.getIncomingService().findProducerNamesByPrefix(prefix, pageNum, pageSize);
        return new JSONReturn(result).toString();
    }


}
