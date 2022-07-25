package com.xdmy.dao;

import com.xdmy.dao.inter.*;
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

    public IShipmentDao getShipmentDao() {
        return shipmentDao;
    }
}
