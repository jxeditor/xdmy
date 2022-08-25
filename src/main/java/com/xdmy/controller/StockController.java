package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Stock;
import com.xdmy.utils.ErrorCode;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/stock")
public class StockController extends BaseController {

    @RequestMapping("/findAllStock")
    public String findAllStock(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "productName", defaultValue = "") String productName
    ) {
        JSONObject result = serviceFacade.getStockService().findAllStock(pageNum, pageSize, productName);
        return new JSONReturn(result).toString();
    }

    @RequestMapping(value = "/addStock")
    public String addStock(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        Stock stock = new Stock();
        stock.setProduct(params.get("product"));
        stock.setUnitstock(Integer.parseInt(params.get("unitstock")));
        stock.setUnitprice(Double.parseDouble(params.get("unitprice")));
        int result = serviceFacade.getStockService().addStock(stock);
        if (result > 0) {
            return new JSONReturn("success", "插入成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.INSERT_FAILED).toString();
        }
    }

    @RequestMapping(value = "/updateStock")
    public String updateStock(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        Stock stock = new Stock();
        stock.setId(Integer.parseInt(params.get("id")));
        stock.setProduct(params.get("product"));
        stock.setUnitstock(Integer.parseInt(params.get("unitstock")));
        stock.setUnitprice(Double.parseDouble(params.get("unitprice")));
        int result = serviceFacade.getStockService().updateStock(stock);
        if (result > 0) {
            return new JSONReturn("success", "更新成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.UPDATE_FAILED).toString();
        }
    }

    @RequestMapping("/deleteStockById")
    public String deleteStockById(@RequestParam(value = "id", defaultValue = "-1") Integer id) {
        int result = serviceFacade.getStockService().deleteStockById(id);
        if (result > 0) {
            return new JSONReturn("success", "删除成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.DELETE_FAILED).toString();
        }
    }
}
