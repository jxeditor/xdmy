package com.xdmy.dao.inter.impl;


import com.xdmy.dao.inter.IAdminDao;
import com.xdmy.domain.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdminDao extends BaseDao implements IAdminDao {

    @Override
    public List<User> verifyLogin(User user) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), user.getUsername(), user.getPassword());
    }

    static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            user.setCompanyName(rs.getString("company_name"));
            return user;
        }
    }
}
