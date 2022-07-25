package com.xdmy.dao.inter.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class BaseDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
}
