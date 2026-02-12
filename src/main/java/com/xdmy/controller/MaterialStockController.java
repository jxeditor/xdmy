package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.MaterialStock;
import com.xdmy.utils.ErrorCode;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/materialStock")
public class MaterialStockController extends BaseController {

    @RequestMapping("/findAllMaterialStock")
    public String findAllMaterialStock(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                       @RequestParam(value = "materialName", defaultValue = "") String materialName,
                                       @RequestParam(value = "hideZeroStock", defaultValue = "false") Boolean hideZeroStock,
                                       HttpServletRequest request
    ) {
        System.out.println("Received hideZeroStock parameter: " + hideZeroStock);
        String companyName = getCompanyName(request);
        JSONObject result = serviceFacade.getMaterialStockService().findAllMaterialStock(pageNum, pageSize, materialName, hideZeroStock, companyName);
        return new JSONReturn(result).toString();
    }

    @RequestMapping(value = "/addMaterialStock")
    public String addMaterialStock(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        String companyName = getCompanyName(request);
        MaterialStock materialStock = new MaterialStock();
        materialStock.setMaterialName(params.get("materialName"));
        materialStock.setUnitstock(Integer.parseInt(params.get("unitstock")));
        materialStock.setCompany_name(companyName);
        int result = serviceFacade.getMaterialStockService().addMaterialStock(materialStock, companyName);
        if (result > 0) {
            return new JSONReturn("success", "插入成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.INSERT_FAILED).toString();
        }
    }

    @RequestMapping(value = "/updateMaterialStock")
    public String updateMaterialStock(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        String companyName = getCompanyName(request);
        MaterialStock materialStock = new MaterialStock();
        materialStock.setId(Integer.parseInt(params.get("id")));
        materialStock.setMaterialName(params.get("materialName"));
        materialStock.setUnitstock(Integer.parseInt(params.get("unitstock")));
        materialStock.setCompany_name(companyName);
        int result = serviceFacade.getMaterialStockService().updateMaterialStock(materialStock, companyName);
        if (result > 0) {
            return new JSONReturn("success", "更新成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.UPDATE_FAILED).toString();
        }
    }

    @RequestMapping("/deleteMaterialStockById")
    public String deleteMaterialStockById(@RequestParam(value = "id", defaultValue = "-1") Integer id,
                                         HttpServletRequest request) {
        int result = serviceFacade.getMaterialStockService().deleteMaterialStockById(id);
        if (result > 0) {
            return new JSONReturn("success", "删除成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.DELETE_FAILED).toString();
        }
    }

    @RequestMapping("/batchDeleteMaterialStock")
    public String batchDeleteMaterialStock(@RequestParam(value = "ids") String ids,
                                         HttpServletRequest request) {
        int result = serviceFacade.getMaterialStockService().batchDeleteMaterialStock(ids);
        if (result > 0) {
            return new JSONReturn("success", "批量删除成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.DELETE_FAILED).toString();
        }
    }

    @RequestMapping("/findMaterialNamesByPrefix")
    public String findMaterialNamesByPrefix(@RequestBody java.util.Map<String, Object> request, HttpServletRequest httpRequest) {
        String prefix = (String) request.getOrDefault("prefix", "");
        String companyName = getCompanyName(httpRequest);
        int pageNum = request.containsKey("pageNum") ? Integer.parseInt(request.get("pageNum").toString()) : 1;
        int pageSize = request.containsKey("pageSize") ? Integer.parseInt(request.get("pageSize").toString()) : 10;
        JSONObject result = serviceFacade.getMaterialStockService().findMaterialNamesByPrefix(prefix, pageNum, pageSize, companyName);
        return new JSONReturn(result).toString();
    }

    @RequestMapping("/operateMaterialStock")
    public String operateMaterialStock(@RequestParam(value = "materialName", defaultValue = "") String materialName,
                                       @RequestParam(value = "quantity", defaultValue = "0") Integer quantity,
                                       @RequestParam(value = "isIncrease", defaultValue = "false") Boolean isIncrease,
                                       HttpServletRequest request
    ) {
        String companyName = getCompanyName(request);
        int result = serviceFacade.getMaterialStockService().operateMaterialStock(materialName, quantity, isIncrease, companyName);
        if (result > 0) {
            return new JSONReturn("success", "操作成功", 1).toString();
        } else {
            return new JSONReturn("error", "操作失败", 0).toString();
        }
    }
}
