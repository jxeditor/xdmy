package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Turnover;
import com.xdmy.utils.ErrorCode;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/turnover")
public class TurnoverController extends BaseController {

    @RequestMapping("/findAllTurnover")
    public String findAllTurnover(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                  @RequestParam(value = "payerName", defaultValue = "") String payerName,
                                  @RequestParam(value = "payeeName", defaultValue = "") String payeeName,
                                  @RequestParam(value = "bizStartDate", defaultValue = "undefined") String bizStartDate,
                                  @RequestParam(value = "bizEndDate", defaultValue = "undefined") String bizEndDate
    ) {
        JSONObject result = serviceFacade.getTurnoverService().findAllTurnover(pageNum, pageSize, payerName, payeeName, bizStartDate, bizEndDate);
        return new JSONReturn(result).toString();
    }

    @RequestMapping(value = "/addTurnover")
    public String addTurnover(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        Turnover turnover = new Turnover();
        turnover.setPayer(params.get("payer"));
        turnover.setPayee(params.get("payee"));
        turnover.setBilldate(params.get("billdate"));
        turnover.setMoney(Double.parseDouble(params.get("money")));
        turnover.setTax(Double.parseDouble(params.get("tax")));
        turnover.setPaid(Double.parseDouble(params.get("money")) + Double.parseDouble(params.get("tax")));
        turnover.setRemark(params.get("remark"));
        int result = serviceFacade.getTurnoverService().addTurnover(turnover);
        if (result > 0) {
            return new JSONReturn("success", "插入成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.INSERT_FAILED).toString();
        }
    }

    @RequestMapping(value = "/updateTurnover")
    public String updateTurnover(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        Turnover turnover = new Turnover();
        turnover.setId(Integer.parseInt(params.get("id")));
        turnover.setPayer(params.get("payer"));
        turnover.setPayee(params.get("payee"));
        turnover.setBilldate(params.get("billdate"));
        turnover.setMoney(Double.parseDouble(params.get("money")));
        turnover.setTax(Double.parseDouble(params.get("tax")));
        turnover.setPaid(Double.parseDouble(params.get("money")) + Double.parseDouble(params.get("tax")));
        turnover.setRemark(params.get("remark"));
        int result = serviceFacade.getTurnoverService().updateTurnover(turnover);
        if (result > 0) {
            return new JSONReturn("success", "更新成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.UPDATE_FAILED).toString();
        }
    }

    @RequestMapping("/deleteTurnoverById")
    public String deleteTurnoverById(@RequestParam(value = "id", defaultValue = "-1") Integer id) {
        int result = serviceFacade.getTurnoverService().deleteTurnoverById(id);
        if (result > 0) {
            return new JSONReturn("success", "删除成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.DELETE_FAILED).toString();
        }
    }
}
