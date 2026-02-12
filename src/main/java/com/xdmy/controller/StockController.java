package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Stock;
import com.xdmy.utils.ErrorCode;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.RequestBody;
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
                               @RequestParam(value = "productName", defaultValue = "") String productName,
                               @RequestParam(value = "hideZeroStock", defaultValue = "false") Boolean hideZeroStock,
                               HttpServletRequest request
    ) {
        System.out.println("Received hideZeroStock parameter: " + hideZeroStock);
        String companyName = getCompanyName(request);
        JSONObject result = serviceFacade.getStockService().findAllStock(pageNum, pageSize, productName, hideZeroStock, companyName);
        return new JSONReturn(result).toString();
    }

    @RequestMapping(value = "/addStock")
    public String addStock(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        String companyName = getCompanyName(request);
        Stock stock = new Stock();
        stock.setProduct(params.get("product"));
        stock.setUnitstock(Integer.parseInt(params.get("unitstock")));
        stock.setUnitprice(Double.parseDouble(params.get("unitprice")));
        stock.setCompany_name(companyName);
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
        String companyName = getCompanyName(request);
        Stock stock = new Stock();
        stock.setId(Integer.parseInt(params.get("id")));
        stock.setProduct(params.get("product"));
        stock.setUnitstock(Integer.parseInt(params.get("unitstock")));
        stock.setUnitprice(Double.parseDouble(params.get("unitprice")));
        stock.setStockstatus(params.get("stockstatus"));
        stock.setCompany_name(companyName);
        int result = serviceFacade.getStockService().updateStock(stock);
        if (result > 0) {
            return new JSONReturn("success", "更新成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.UPDATE_FAILED).toString();
        }
    }

    @RequestMapping("/deleteStockById")
    public String deleteStockById(@RequestParam(value = "id", defaultValue = "-1") Integer id,
                                 HttpServletRequest request) {
        int result = serviceFacade.getStockService().deleteStockById(id);
        if (result > 0) {
            return new JSONReturn("success", "删除成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.DELETE_FAILED).toString();
        }
    }

    @RequestMapping("/batchDeleteStock")
    public String batchDeleteStock(@RequestParam(value = "ids") String ids,
                                  HttpServletRequest request) {
        int result = serviceFacade.getStockService().batchDeleteStock(ids);
        if (result > 0) {
            return new JSONReturn("success", "批量删除成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.DELETE_FAILED).toString();
        }
    }

    @RequestMapping("/findProductNamesByPrefix")
    public String findProductNamesByPrefix(@RequestBody java.util.Map<String, Object> request, HttpServletRequest httpRequest) {
        String prefix = (String) request.getOrDefault("prefix", "");
        String companyName = getCompanyName(httpRequest);
        int pageNum = request.containsKey("pageNum") ? Integer.parseInt(request.get("pageNum").toString()) : 1;
        int pageSize = request.containsKey("pageSize") ? Integer.parseInt(request.get("pageSize").toString()) : 10;
        JSONObject result = serviceFacade.getStockService().findProductNamesByPrefix(prefix, pageNum, pageSize, companyName);
        return new JSONReturn(result).toString();
    }

    @RequestMapping("/flattenStock")
    public String flattenStock(HttpServletRequest request) {
        String companyName = getCompanyName(request);
        int result = serviceFacade.getStockService().flattenStock(companyName);
        if (result > 0) {
            return new JSONReturn("success", "库存抹平成功", 1).toString();
        } else {
            return new JSONReturn("error", "库存抹平失败", 0).toString();
        }
    }

    @RequestMapping("/getFlattenStockCount")
    public String getFlattenStockCount(HttpServletRequest request) {
        String companyName = getCompanyName(request);
        int count = serviceFacade.getStockService().getFlattenStockCount(companyName);
        JSONObject result = new JSONObject();
        result.put("data", count);
        return new JSONReturn(result).toString();
    }

    @RequestMapping("/checkMaterialExist")
    public String checkMaterialExist(@RequestParam(value = "materialName") String materialName,
                                    HttpServletRequest request) {
        String companyName = getCompanyName(request);
        boolean exists = serviceFacade.getMaterialStockService().checkMaterialExist(materialName, companyName);
        if (exists) {
            return new JSONReturn("success", "原材料存在", 1).toString();
        } else {
            return new JSONReturn("error", "未找到原材料库存信息", 0).toString();
        }
    }

    @RequestMapping("/operateMaterialStock")
    public String operateMaterialStock(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        String companyName = getCompanyName(request);
        String materialName = params.get("materialName");
        String stockChangeStr = params.get("stockChange");
        
        // 非空校验
        if (materialName == null || materialName.isEmpty() || stockChangeStr == null || stockChangeStr.isEmpty()) {
            return new JSONReturn(ErrorCode.PARAMS_ERROR).toString();
        }
        
        try {
            int stockChange = Integer.parseInt(stockChangeStr);
            boolean isIncrease = stockChange > 0;
            int quantity = Math.abs(stockChange);
            int result = serviceFacade.getMaterialStockService().operateMaterialStock(materialName, quantity, isIncrease, companyName);
            if (result > 0) {
                return new JSONReturn("success", "操作成功", 1).toString();
            } else {
                return new JSONReturn("error", "操作失败", 0).toString();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new JSONReturn(ErrorCode.PARAMS_ERROR).toString();
        }
    }

    @RequestMapping("/findMaterialOperations")
    public String findMaterialOperations(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "materialName", defaultValue = "") String materialName,
                                         @RequestParam(value = "operationType", defaultValue = "") String operationType,
                                         @RequestParam(value = "startDate", defaultValue = "") String startDate,
                                         @RequestParam(value = "endDate", defaultValue = "") String endDate,
                                         HttpServletRequest request) {
        String companyName = getCompanyName(request);
        JSONObject result = serviceFacade.getMaterialStockService().findMaterialOperations(pageNum, pageSize, materialName, operationType, startDate, endDate, companyName);
        return new JSONReturn(result).toString();
    }
}
