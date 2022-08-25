package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Shipment;


public interface IShipmentService {
    JSONObject findAllShipment(int pageNum, int pageSize, String customerName, String productName, String bizStartDate, String bizEndDate);

    JSONObject getShipmentStatement(String customerName, String bizStartDate, String bizEndDate);

    int addShipment(Shipment shipment);

    int deleteShipmentById(int id);

    int updateShipment(Shipment shipment);

    int updatePaystatusShipmentById(int id);
}
