package com.xdmy.controller;

import com.xdmy.service.ServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class BaseController extends Controller {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ServiceFacade serviceFacade;
}
