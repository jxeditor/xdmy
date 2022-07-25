package com.xdmy.service.inter.impl;

import com.xdmy.dao.DaoFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class BaseService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DaoFacade daoFacade;
}
