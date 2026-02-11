package com.xdmy.service;

import com.xdmy.service.inter.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 外观模式
 * Service
 */
@Service
public class ServiceFacade {
    @Autowired
    IShipmentService shipmentService;

    @Autowired
    IIncomingService incomingService;

    @Autowired
    IStockService stockService;

    @Autowired
    IMaterialStockService materialStockService;

    @Autowired
    IProductMaterialRelationService productMaterialRelationService;

    @Autowired
    IProductService productService;

    @Autowired
    IAdminService adminService;

    @Autowired
    ITurnoverService turnoverService;

    @Autowired
    IScreenService screenService;

    public IShipmentService getShipmentService() {
        return shipmentService;
    }

    public IIncomingService getIncomingService() {
        return incomingService;
    }

    public IStockService getStockService() {
        return stockService;
    }

    public IMaterialStockService getMaterialStockService() {
        return materialStockService;
    }

    public IProductMaterialRelationService getProductMaterialRelationService() {
        return productMaterialRelationService;
    }

    public IProductService getProductService() {
        return productService;
    }

    public IAdminService getAdminService() {
        return adminService;
    }

    public ITurnoverService getTurnoverService() {
        return turnoverService;
    }

    public IScreenService getScreenService() {
        return screenService;
    }
}