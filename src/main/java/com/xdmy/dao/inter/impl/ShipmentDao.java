package com.xdmy.dao.inter.impl;


import com.xdmy.dao.inter.IShipmentDao;
import com.xdmy.domain.Shipment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ShipmentDao extends BaseDao implements IShipmentDao {

    @Override
    public List<Shipment> findAllShipment(int pageNum, int pageSize, String customerName, String productName, String bizStartDate, String bizEndDate) {
        int currOffset = (pageNum - 1) * pageSize;
        String sql = "SELECT * FROM shipment WHERE 1=1 AND is_delete = 0";
        sql = genFilterSql(sql, customerName, productName, bizStartDate, bizEndDate);
        sql += " ORDER BY billdate DESC,odd DESC,id DESC LIMIT ? ,?";
        return jdbcTemplate.query(sql, new Object[]{currOffset, pageSize}, new ShipmentRowMapper());
    }

    @Override
    public List<Shipment> getShipmentStatement(String customerName, String bizStartDate, String bizEndDate) {
        String sql = "SELECT * FROM shipment WHERE 1=1 AND is_delete = 0 AND paystatus = 0" +
                " AND customer = ? AND billdate >= ? AND billdate <= ?";
        sql += " ORDER BY billdate,create_time";
        return jdbcTemplate.query(sql, new Object[]{customerName, bizStartDate, bizEndDate}, new ShipmentRowMapper());
    }

    @Override
    public int getAllTotalSize(String customerName, String productName, String bizStartDate, String bizEndDate) {
        String sql = "SELECT count(1) FROM shipment WHERE 1=1 AND is_delete = 0";
        sql = genFilterSql(sql, customerName, productName, bizStartDate, bizEndDate);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public int getDistinctSize(String customerName, String bizStartDate, String bizEndDate) {
        String sql = "SELECT count(distinct odd) FROM shipment WHERE 1=1 AND is_delete = 0 AND paystatus = 0" +
                " AND customer = ? AND billdate >= ? AND billdate <= ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, customerName, bizStartDate, bizEndDate);
    }

    @Override
    public double getSumPay(String customerName, String bizStartDate, String bizEndDate) {
        String sql = "SELECT sum(money) FROM shipment WHERE 1=1 AND is_delete = 0 AND paystatus = 0" +
                " AND customer = ? AND billdate >= ? AND billdate <= ?";
        return jdbcTemplate.queryForObject(sql, Double.class, customerName, bizStartDate, bizEndDate);
    }

    @Override
    public int addShipment(Shipment shipment) {
        String sql = "INSERT INTO shipment(odd,customer,product,billdate,amount,unitprice,money,paystatus,boardcost,fireproofboardcost,costmoney,profit) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, shipment.getOdd(), shipment.getCustomer(), shipment.getProduct(), shipment.getBilldate(), shipment.getAmount(), shipment.getUnitprice(), shipment.getMoney(), shipment.getPaystatus(), shipment.getBoardcost(), shipment.getFireproofboardcost(), shipment.getCostmoney(), shipment.getProfit());
    }

    @Override
    public int deleteShipmentById(int id) {
        String sql = "UPDATE shipment set is_delete = 1 WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int updateShipment(Shipment shipment) {
        String sql = "UPDATE shipment set odd = ?,customer = ?,product = ?,billdate = ?,amount = ?,unitprice = ?,money = ?" +
                ",paystatus = ?,boardcost = ?,fireproofboardcost = ?,costmoney = ?,profit = ?" +
                "WHERE id = ? ";
        return jdbcTemplate.update(sql, shipment.getOdd(), shipment.getCustomer(), shipment.getProduct(), shipment.getBilldate(), shipment.getAmount(), shipment.getUnitprice(), shipment.getMoney(), shipment.getPaystatus(), shipment.getBoardcost(), shipment.getFireproofboardcost(), shipment.getCostmoney(), shipment.getProfit(), shipment.getId());
    }

    @Override
    public int updatePaystatusShipmentById(int id) {
        String sql = "UPDATE shipment set paystatus = '1' WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    static class ShipmentRowMapper implements RowMapper<Shipment> {
        @Override
        public Shipment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Shipment shipment = new Shipment();
            shipment.setId(rs.getInt("id"));
            shipment.setOdd(rs.getString("odd"));
            shipment.setCustomer(rs.getString("customer"));
            shipment.setProduct(rs.getString("product"));
            shipment.setBilldate(rs.getString("billdate"));
            shipment.setAmount(rs.getInt("amount"));
            shipment.setUnitprice(rs.getDouble("unitprice"));
            shipment.setMoney(rs.getDouble("money"));
            shipment.setPaystatus(rs.getString("paystatus"));
            shipment.setBoardcost(rs.getDouble("boardcost"));
            shipment.setFireproofboardcost(rs.getDouble("fireproofboardcost"));
            shipment.setCostmoney(rs.getDouble("costmoney"));
            shipment.setProfit(rs.getDouble("profit"));
            return shipment;
        }
    }

    public String genFilterSql(String sql, String customerName, String productName, String bizStartDate, String bizEndDate) {
        if (!customerName.equals("")) {
            sql += " AND customer LIKE '%" + customerName + "%'";
        }
        if (!productName.equals("")) {
            sql += " AND product LIKE '%" + productName + "%'";
        }
        if (!bizStartDate.equals("undefined")) {
            sql += " AND billdate >= '" + bizStartDate + "'";
        }
        if (!bizEndDate.equals("undefined")) {
            sql += " AND billdate <= '" + bizEndDate + "'";
        }
        return sql;
    }

}
