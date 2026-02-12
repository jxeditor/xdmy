package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Shipment;


public interface IShipmentService {
    JSONObject findAllShipment(int pageNum, int pageSize, String customerName, String productName, String bizStartDate, String bizEndDate, String companyName);

    JSONObject getShipmentStatement(String customerName, String bizStartDate, String bizEndDate, String companyName);

    int addShipment(Shipment shipment, String companyName);

    int deleteShipmentById(int id, String companyName);

    int updateShipment(Shipment shipment, String materialRelationsStr, String companyName);

    int updatePaystatusShipmentById(int id);

    JSONObject findCustomerNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);

    JSONObject findMaterialOperationsByShipmentId(int shipmentId);

    Shipment findShipmentById(int id);
}
