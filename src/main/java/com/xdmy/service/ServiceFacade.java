package com.xdmy.service;

import com.xdmy.controller.StockController;
import com.xdmy.controller.TurnoverController;
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
    IAdminService adminService;

    @Autowired
    ITurnoverService turnoverService;

    public IShipmentService getShipmentService() {
        return shipmentService;
    }

    public IIncomingService getIncomingService() {
        return incomingService;
    }

    public IStockService getStockService() {
        return stockService;
    }

    public IAdminService getAdminService() {
        return adminService;
    }

    public ITurnoverService getTurnoverService() {
        return turnoverService;
    }
}