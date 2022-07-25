package com.xdmy.dao.inter;

import com.xdmy.domain.Shipment;

import java.util.List;

public interface IShipmentDao {
    List<Shipment> findAllShipment(int pageNum, int pageSize, String customerName, String bizStartDate, String bizEndDate);

    int getAllTotalSize(String customerName, String bizStartDate, String bizEndDate);

    int addShipment(Shipment shipment);

    int deleteShipmentById(int id);

    int updateShipment(Shipment shipment);

    int updatePaystatusShipmentById(int id);
}
