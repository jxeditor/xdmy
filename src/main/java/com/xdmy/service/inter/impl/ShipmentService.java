package com.xdmy.service.inter.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xdmy.datasource.DBContextHolder;
import com.xdmy.domain.Shipment;
import com.xdmy.service.inter.IShipmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentService extends BaseService implements IShipmentService {
    @Override
    public JSONObject findAllShipment(int pageNum, int pageSize, String customerName, String productName, String bizStartDate, String bizEndDate) {
        DBContextHolder.setDbType("primary");
        List<Shipment> shipmentList = daoFacade.getShipmentDao().findAllShipment(pageNum, pageSize, customerName, productName, bizStartDate, bizEndDate);
        int total = daoFacade.getShipmentDao().getAllTotalSize(customerName, productName, bizStartDate, bizEndDate);
        JSONObject result = toJSONObject(shipmentList);
        result.put("total", total);
        return result;
    }

    @Override
    public JSONObject getShipmentStatement(String customerName, String bizStartDate, String bizEndDate) {
        DBContextHolder.setDbType("primary");
        List<Shipment> shipmentList = daoFacade.getShipmentDao().getShipmentStatement(customerName, bizStartDate, bizEndDate);
        int total = daoFacade.getShipmentDao().getDistinctSize(customerName, bizStartDate, bizEndDate);
        double sumpay = daoFacade.getShipmentDao().getSumPay(customerName, bizStartDate, bizEndDate);
        JSONObject result = toJSONObject(shipmentList);
        result.put("total", total);
        result.put("sumpay", sumpay);
        return result;
    }

    @Override
    public int addShipment(Shipment shipment) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getShipmentDao().addShipment(shipment);
    }

    @Override
    public int deleteShipmentById(int id) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getShipmentDao().deleteShipmentById(id);
    }

    @Override
    public int updateShipment(Shipment shipment) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getShipmentDao().updateShipment(shipment);
    }

    @Override
    public int updatePaystatusShipmentById(int id) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getShipmentDao().updatePaystatusShipmentById(id);
    }

    public JSONObject toJSONObject(List<Shipment> shipmentList) {
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        try {
            if (shipmentList != null) {
                for (Shipment shipment : shipmentList) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", shipment.getId());
                    obj.put("odd", shipment.getOdd());
                    obj.put("customer", shipment.getCustomer());
                    obj.put("product", shipment.getProduct());
                    obj.put("billdate", shipment.getBilldate());
                    obj.put("amount", shipment.getAmount());
                    obj.put("unitprice", shipment.getUnitprice());
                    obj.put("money", shipment.getMoney());
                    obj.put("paystatus", shipment.getPaystatus());
                    obj.put("boardcost", shipment.getBoardcost());
                    obj.put("fireproofboardcost", shipment.getFireproofboardcost());
                    obj.put("costmoney", shipment.getCostmoney());
                    obj.put("profit", shipment.getProfit());
                    data.add(obj);
                }
            }
            result.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
