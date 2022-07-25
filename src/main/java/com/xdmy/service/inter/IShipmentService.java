package com.xdmy.service.inter;

import com.xdmy.domain.Shipment;
import org.json.JSONObject;


public interface IShipmentService {
    JSONObject findAllShipment(int pageNum, int pageSize, String customerName, String bizStartDate, String bizEndDate);

    int addShipment(Shipment shipment);

    int deleteShipmentById(int id);

    int updateShipment(Shipment shipment);

    int updatePaystatusShipmentById(int id);
}
