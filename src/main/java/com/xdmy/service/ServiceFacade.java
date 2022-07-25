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

    public IShipmentService getShipmentService() {
        return shipmentService;
    }
}