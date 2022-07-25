package com.xdmy.controller;

import com.xdmy.domain.Shipment;
import com.xdmy.utils.ErrorCode;
import com.xdmy.utils.JSONReturn;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/shipment")
public class ShipmentController extends BaseController {

    @RequestMapping("/findAllShipment")
    public String findAllOrder(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "customerName", defaultValue = "") String customerName,
                               @RequestParam(value = "bizStartDate", defaultValue = "undefined") String bizStartDate,
                               @RequestParam(value = "bizEndDate", defaultValue = "undefined") String bizEndDate
    ) throws Exception {
        JSONObject result = serviceFacade.getShipmentService().findAllShipment(pageNum, pageSize, customerName, bizStartDate, bizEndDate);
        return new JSONReturn(result).toString();
    }

    @RequestMapping(value = "/addShipment")
    public String addShipment(HttpServletRequest request) throws Exception {
        HashMap<String, String> params = getRequestParam(request);
        System.out.println(params.toString());
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
        shipment.setCostmoney((Double.parseDouble(params.get("boardcost") + Double.parseDouble(params.get("fireproofboardcost"))) * Integer.parseInt(params.get("amount"))));
        int result = serviceFacade.getShipmentService().addShipment(shipment);
        if (result > 0) {
            return new JSONReturn("success", "插入成功", 1).toString();
        } else {
            return new JSONReturn(ErrorCode.INSERT_ERROR).toString();
        }
    }
}
