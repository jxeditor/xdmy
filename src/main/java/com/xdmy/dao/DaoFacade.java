package com.xdmy.dao;

import com.xdmy.dao.inter.*;
import com.xdmy.service.inter.IIncomingService;
import com.xdmy.service.inter.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 外观模式
 * Dao
 */
@Repository
public class DaoFacade {
    @Autowired
    IShipmentDao shipmentDao;

    @Autowired
    IIncomingDao incomingDao;

    @Autowired
    IStockDao stockDao;

    public IShipmentDao getShipmentDao() {
        return shipmentDao;
    }

    public IIncomingDao getIncomingDao() {
        return incomingDao;
    }

    public IStockDao getStockDao() {
            return stockDao;
    }
}
