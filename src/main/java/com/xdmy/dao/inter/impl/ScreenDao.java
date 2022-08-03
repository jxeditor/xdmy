package com.xdmy.dao.inter.impl;


import com.xdmy.dao.inter.IScreenDao;
import org.json.JSONObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ScreenDao extends BaseDao implements IScreenDao {

    @Override
    public List<JSONObject> getShipment1ChartData(String customerName, String bizStartDate, String bizEndDate) {
        String sql = "SELECT billdate,sum(money) money,sum(costmoney) costmoney,sum(profit) profit" +
                " ,sum(if(paystatus = 1,money,0)) paymoney" +
                " ,sum(if(paystatus = 1,profit,0)) payprofit " +
                "FROM shipment " +
                "WHERE is_delete = 0";
        sql = genFilterSql(sql, customerName, bizStartDate, bizEndDate);
        sql += " GROUP BY billdate ORDER BY billdate DESC";
        return jdbcTemplate.query(sql, new Shipment1ChartDataRowMapper());
    }

    @Override
    public List<JSONObject> getShipment2ChartData() {
        String sql = "SELECT substring(billdate,1,7) billdate,sum(money) money,sum(costmoney) costmoney,sum(profit) profit" +
                " ,sum(if(paystatus = 1,money,0)) paymoney" +
                " ,sum(if(paystatus = 1,profit,0)) payprofit " +
                "FROM shipment " +
                "WHERE is_delete = 0";
        sql += " GROUP BY substring(billdate,1,7) ORDER BY billdate DESC LIMIT 12";
        return jdbcTemplate.query(sql, new Shipment2ChartDataRowMapper());
    }

    static class Shipment1ChartDataRowMapper implements RowMapper<JSONObject> {
        @Override
        public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
            JSONObject result = new JSONObject();
            result.put("billdate", rs.getString("billdate"));
            result.put("money", rs.getDouble("money"));
            result.put("costmoney", rs.getDouble("costmoney"));
            result.put("profit", rs.getDouble("profit"));
            result.put("paymoney", rs.getDouble("paymoney"));
            result.put("payprofit", rs.getDouble("payprofit"));
            return result;
        }
    }

    static class Shipment2ChartDataRowMapper implements RowMapper<JSONObject> {
        @Override
        public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
            JSONObject result = new JSONObject();
            result.put("billdate", rs.getString("billdate"));
            result.put("money", rs.getDouble("money"));
            result.put("costmoney", rs.getDouble("costmoney"));
            result.put("profit", rs.getDouble("profit"));
            result.put("paymoney", rs.getDouble("paymoney"));
            result.put("payprofit", rs.getDouble("payprofit"));
            return result;
        }
    }

    public String genFilterSql(String sql, String customerName, String bizStartDate, String bizEndDate) {
        if (!customerName.equals("")) {
            sql += " AND customer LIKE '%" + customerName + "%'";
        }
        if (!bizStartDate.equals("undefined")) {
            sql += " AND billdate >= '" + bizStartDate + "'";
        } else {
            sql += " AND billdate >= CURRENT_DATE - INTERVAL '7' DAY";
        }
        if (!bizEndDate.equals("undefined")) {
            sql += " AND billdate <= '" + bizEndDate + "'";
        } else {
            sql += " AND billdate <= CURRENT_DATE";
        }
        return sql;
    }
}
