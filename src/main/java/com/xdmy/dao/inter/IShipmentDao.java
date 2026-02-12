package com.xdmy.dao.inter;

import com.xdmy.domain.Shipment;

import java.util.List;

public interface IShipmentDao {
    List<Shipment> findAllShipment(int pageNum, int pageSize, String customerName, String productName, String bizStartDate, String bizEndDate, String companyName);

    List<Shipment> getShipmentStatement(String customerName, String bizStartDate, String bizEndDate, String companyName);

    int getAllTotalSize(String customerName, String productName, String bizStartDate, String bizEndDate, String companyName);


    int getDistinctSize(String customerName, String bizStartDate, String bizEndDate, String companyName);

    double getSumPay(String customerName, String bizStartDate, String bizEndDate, String companyName);

    int addShipment(Shipment shipment, String companyName);

    int deleteShipmentById(int id, String companyName);
    
    int updateShipment(Shipment shipment, String materialRelationsStr, String companyName);

    int updatePaystatusShipmentById(int id);

    List<String> findCustomerNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);

    int getCustomerNamesCount(String prefix,String companyName);

    List<java.util.Map<String, Object>> findMaterialOperationsByShipmentId(int shipmentId);

    Shipment findShipmentById(int id);
}
