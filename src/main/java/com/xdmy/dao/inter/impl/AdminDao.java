package com.xdmy.dao.inter.impl;


import com.xdmy.dao.inter.IAdminDao;
import com.xdmy.dao.inter.IStockDao;
import com.xdmy.domain.Stock;
import com.xdmy.domain.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdminDao extends BaseDao implements IAdminDao {

    @Override
    public List<User> verifyLogin(User user) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        return jdbcTemplate.query(sql, new Object[]{user.getUsername(), user.getPassword()}, new UserRowMapper());
    }

    static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            return user;
        }
    }
}
