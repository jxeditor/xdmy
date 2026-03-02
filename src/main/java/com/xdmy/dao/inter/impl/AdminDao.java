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

    @Override
    public void updateToken(int userId, String token) {
        // 保留原方法，用于向后兼容
        String sql = "UPDATE users SET token = ? WHERE id = ?";
        jdbcTemplate.update(sql, token, userId);
    }

    public void saveToken(int userId, String token) {
        String sql = "INSERT INTO user_tokens (user_id, token) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, token);
    }

    @Override
    public User getUserByToken(String token) {
        // 先从新的token表查询
        String sql = "SELECT u.* FROM users u JOIN user_tokens ut ON u.id = ut.user_id WHERE ut.token = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), token);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        // 兼容旧的token存储方式
        sql = "SELECT * FROM users WHERE token = ?";
        users = jdbcTemplate.query(sql, new UserRowMapper(), token);
        return users.isEmpty() ? null : users.get(0);
    }

    public void deleteUserTokens(int userId) {
        String sql = "DELETE FROM user_tokens WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
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
            user.setToken(rs.getString("token"));
            return user;
        }
    }
}
