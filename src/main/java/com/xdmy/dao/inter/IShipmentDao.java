package com.xdmy.dao.inter;

import com.xdmy.domain.Shipment;

import java.util.List;

public interface IShipmentDao {
    List<Shipment> findAllShipment(int pageNum, int pageSize, String customerName, String productName, String bizStartDate, String bizEndDate);

    List<Shipment> getShipmentStatement(String customerName, String bizStartDate, String bizEndDate);

    int getAllTotalSize(String customerName, String productName, String bizStartDate, String bizEndDate);


    int getDistinctSize(String customerName, String bizStartDate, String bizEndDate);

    double getSumPay(String customerName, String bizStartDate, String bizEndDate);

    int addShipment(Shipment shipment);

    int deleteShipmentById(int id);

    int updateShipment(Shipment shipment);

    int updatePaystatusShipmentById(int id);

    List<String> findCustomerNamesByPrefix(String prefix, int pageNum, int pageSize);

    int getCustomerNamesCount(String prefix);
}
