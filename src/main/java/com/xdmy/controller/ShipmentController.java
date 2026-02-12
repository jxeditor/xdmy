package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Shipment;
import com.xdmy.utils.ErrorCode;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/shipment")
public class ShipmentController extends BaseController {

    @RequestMapping("/findAllShipment")
    public String findAllShipment(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                  @RequestParam(value = "customerName", defaultValue = "") String customerName,
                                  @RequestParam(value = "productName", defaultValue = "") String productName,
                                  @RequestParam(value = "bizStartDate", defaultValue = "undefined") String bizStartDate,
                                  @RequestParam(value = "bizEndDate", defaultValue = "undefined") String bizEndDate,
                                  HttpServletRequest request
    ) {
        String companyName = getCompanyName(request);
        JSONObject result = serviceFacade.getShipmentService().findAllShipment(pageNum, pageSize, customerName, productName, bizStartDate, bizEndDate, companyName);
        return new JSONReturn(result).toString();
    }

    @RequestMapping(value = "/addShipment")
    public String addShipment(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        String companyName = getCompanyName(request);
        Shipment shipment = new Shipment();
        shipment.setOdd(params.get("odd"));
        shipment.setCustomer(params.get("customer"));
        shipment.setProduct(params.get("product"));
        shipment.setBilldate(params.get("billdate"));
        shipment.setAmount(Integer.parseInt(params.get("amount")));
        shipment.setUnitprice(Double.parseDouble(params.get("unitprice")));
        shipment.setMoney(Integer.parseInt(params.get("amount")) * Double.parseDouble(params.get("unitprice")));
        shipment.setPaystatus(params.get("paystatus"));
        shipment.setBoardcost(Double.parseDouble(params.get("boardcost")));
        shipment.setFireproofboardcost(Double.parseDouble(params.get("fireproofboardcost")));
        shipment.setCostmoney((Double.parseDouble(params.get("boardcost")) + Double.parseDouble(params.get("fireproofboardcost"))) * Integer.parseInt(params.get("amount")));
        shipment.setProfit(Integer.parseInt(params.get("amount")) * Double.parseDouble(params.get("unitprice")) - (Double.parseDouble(params.get("boardcost")) + Double.parseDouble(params.get("fireproofboardcost"))) * Integer.parseInt(params.get("amount")));
        shipment.setRemark(params.get("remark"));
        shipment.setOperate_material(Integer.parseInt(params.get("operate_material")));
        shipment.setCompany_name(companyName);
        int result = serviceFacade.getShipmentService().addShipment(shipment, companyName);
        if (result > 0) {
            return new JSONReturn("success", "插入成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.INSERT_FAILED).toString();
        }
    }

    @RequestMapping(value = "/updateShipment")
    public String updateShipment(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        String companyName = getCompanyName(request);
        Shipment shipment = new Shipment();
        shipment.setId(Integer.parseInt(params.get("id")));
        shipment.setOdd(params.get("odd"));
        shipment.setCustomer(params.get("customer"));
        shipment.setProduct(params.get("product"));
        shipment.setBilldate(params.get("billdate"));
        shipment.setAmount(Integer.parseInt(params.get("amount")));
        shipment.setUnitprice(Double.parseDouble(params.get("unitprice")));
        shipment.setMoney(Integer.parseInt(params.get("amount")) * Double.parseDouble(params.get("unitprice")));
        shipment.setPaystatus(params.get("paystatus"));
        shipment.setBoardcost(Double.parseDouble(params.get("boardcost")));
        shipment.setFireproofboardcost(Double.parseDouble(params.get("fireproofboardcost")));
        shipment.setCostmoney((Double.parseDouble(params.get("boardcost")) + Double.parseDouble(params.get("fireproofboardcost"))) * Integer.parseInt(params.get("amount")));
        shipment.setProfit(Integer.parseInt(params.get("amount")) * Double.parseDouble(params.get("unitprice")) - (Double.parseDouble(params.get("boardcost")) + Double.parseDouble(params.get("fireproofboardcost"))) * Integer.parseInt(params.get("amount")));
        shipment.setRemark(params.get("remark"));
        shipment.setOperate_material(Integer.parseInt(params.get("operate_material")));
        shipment.setCompany_name(companyName);
        // 获取原材料关系数据
        String materialRelationsStr = params.get("materialRelations");
        int result = serviceFacade.getShipmentService().updateShipment(shipment, materialRelationsStr, companyName);
        if (result > 0) {
            return new JSONReturn("success", "更新成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.UPDATE_FAILED).toString();
        }
    }

    @RequestMapping("/deleteShipmentById")
    public String deleteShipmentById(@RequestParam(value = "id", defaultValue = "-1") Integer id,
                                    HttpServletRequest request) {
        String companyName = getCompanyName(request);
        int result = serviceFacade.getShipmentService().deleteShipmentById(id, companyName);
        if (result > 0) {
            return new JSONReturn("success", "删除成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.DELETE_FAILED).toString();
        }
    }

    @RequestMapping("/updatePaystatusShipmentById")
    public String updatePaystatusShipmentById(@RequestParam(value = "id", defaultValue = "-1") Integer id,
                                             HttpServletRequest request) {
        int result = serviceFacade.getShipmentService().updatePaystatusShipmentById(id);
        if (result > 0) {
            return new JSONReturn("success", "更新成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.UPDATE_FAILED).toString();
        }
    }

    @RequestMapping("/findCustomerNamesByPrefix")
    public String findCustomerNamesByPrefix(@RequestBody java.util.Map<String, Object> request, HttpServletRequest httpRequest) {
        String prefix = (String) request.getOrDefault("prefix", "");
        String companyName = getCompanyName(httpRequest);
        int pageNum = request.containsKey("pageNum") ? Integer.parseInt(request.get("pageNum").toString()) : 1;
        int pageSize = request.containsKey("pageSize") ? Integer.parseInt(request.get("pageSize").toString()) : 10;
        JSONObject result = serviceFacade.getShipmentService().findCustomerNamesByPrefix(prefix, pageNum, pageSize, companyName);
        return new JSONReturn(result).toString();
    }

    @RequestMapping("/findMaterialOperationsByShipmentId")
    public String findMaterialOperationsByShipmentId(@RequestParam(value = "shipmentId") Integer shipmentId,
                                                   HttpServletRequest request) {
        JSONObject result = serviceFacade.getShipmentService().findMaterialOperationsByShipmentId(shipmentId);
        return new JSONReturn(result).toString();
    }

    @RequestMapping("/findShipmentById")
    public String findShipmentById(@RequestParam(value = "id") Integer id,
                                  HttpServletRequest request) {
        Shipment shipment = serviceFacade.getShipmentService().findShipmentById(id);
        if (shipment != null) {
            JSONObject result = new JSONObject();
            result.put("data", shipment);
            return new JSONReturn(result).toString();
        } else {
            return new JSONReturn("error", "未找到数据", 0).toString();
        }
    }

}
