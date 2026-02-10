package com.xdmy.dao.inter.impl;


import com.xdmy.dao.inter.IShipmentDao;
import com.xdmy.domain.Shipment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

//
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
        sql += " ORDER BY billdate,odd,id";
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
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 添加出货记录
            String sql = "INSERT INTO shipment(odd,customer,product,billdate,amount,unitprice,money,paystatus,boardcost,fireproofboardcost,costmoney,profit,remark) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            int result = jdbcTemplate.update(sql, shipment.getOdd(), shipment.getCustomer(), shipment.getProduct(), shipment.getBilldate(), shipment.getAmount(), shipment.getUnitprice(), shipment.getMoney(), shipment.getPaystatus(), shipment.getBoardcost(), shipment.getFireproofboardcost(), shipment.getCostmoney(), shipment.getProfit(), shipment.getRemark());
            
            // 检查stock表中是否存在对应产品的记录
            String checkSql = "SELECT COUNT(*) FROM stock WHERE product = ?";
            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, shipment.getProduct());
            
            if (count == 0) {
                // 如果不存在，插入一条新记录
                String insertSql = "INSERT INTO stock(product, unitstock, unitprice, inamount, outamount, lastoutdate, stockstatus) " +
                        "VALUES(?, 0, ?, 0, ?, ?, '1')";
                jdbcTemplate.update(insertSql, shipment.getProduct(), shipment.getUnitprice(), shipment.getAmount(), shipment.getBilldate());
            } else {
                // 如果存在，更新outamount和lastoutdate字段
                String updateSql = "UPDATE stock SET outamount = outamount + ?, lastoutdate = ? WHERE product = ?";
                jdbcTemplate.update(updateSql, shipment.getAmount(), shipment.getBilldate(), shipment.getProduct());
            }
            
            // 提交事务
            jdbcTemplate.execute("COMMIT");
            return result;
        } catch (Exception e) {
            // 回滚事务
            jdbcTemplate.execute("ROLLBACK");
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteShipmentById(int id) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 先查询要删除的出货记录的产品和数量
            String querySql = "SELECT * FROM shipment WHERE id = ?";
            Shipment shipment = jdbcTemplate.queryForObject(querySql, new Object[]{id}, new ShipmentRowMapper());
            
            // 删除出货记录
            String sql = "UPDATE shipment set is_delete = 1 WHERE id = ?";
            int result = jdbcTemplate.update(sql, id);
            
            // 更新stock表中对应产品的outamount字段
            String updateSql = "UPDATE stock SET outamount = outamount - ?, lastoutdate = (SELECT MAX(billdate) FROM shipment WHERE product = ? AND is_delete = 0) WHERE product = ?";
            jdbcTemplate.update(updateSql, shipment.getAmount(), shipment.getProduct(), shipment.getProduct());
            
            // 提交事务
            jdbcTemplate.execute("COMMIT");
            return result;
        } catch (Exception e) {
            // 回滚事务
            jdbcTemplate.execute("ROLLBACK");
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int updateShipment(Shipment shipment) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 先查询原出货记录的产品和数量
            String querySql = "SELECT * FROM shipment WHERE id = ?";
            Shipment oldShipment = jdbcTemplate.queryForObject(querySql, new Object[]{shipment.getId()}, new ShipmentRowMapper());
            
            // 更新出货记录
            String sql = "UPDATE shipment set odd = ?,customer = ?,product = ?,billdate = ?,amount = ?,unitprice = ?,money = ?" +
                    ",paystatus = ?,boardcost = ?,fireproofboardcost = ?,costmoney = ?,profit = ?,remark = ?" +
                    "WHERE id = ? ";
            int result = jdbcTemplate.update(sql, shipment.getOdd(), shipment.getCustomer(), shipment.getProduct(), shipment.getBilldate(), shipment.getAmount(), shipment.getUnitprice(), shipment.getMoney(), shipment.getPaystatus(), shipment.getBoardcost(), shipment.getFireproofboardcost(), shipment.getCostmoney(), shipment.getProfit(), shipment.getRemark(), shipment.getId());
            
            // 如果产品没有变化
            if (oldShipment.getProduct().equals(shipment.getProduct())) {
                // 更新stock表中对应产品的outamount字段
                int amountDiff = shipment.getAmount() - oldShipment.getAmount();
                String updateSql = "UPDATE stock SET outamount = outamount + ?, lastoutdate = ? WHERE product = ?";
                jdbcTemplate.update(updateSql, amountDiff, shipment.getBilldate(), shipment.getProduct());
            } else {
                // 如果产品变化了，需要更新两个产品的outamount字段
                // 减少原产品的outamount
                String updateOldSql = "UPDATE stock SET outamount = outamount - ?, lastoutdate = (SELECT MAX(billdate) FROM shipment WHERE product = ? AND is_delete = 0) WHERE product = ?";
                jdbcTemplate.update(updateOldSql, oldShipment.getAmount(), oldShipment.getProduct(), oldShipment.getProduct());
                
                // 增加新产品的outamount
                // 检查新产品是否在stock表中存在
                String checkSql = "SELECT COUNT(*) FROM stock WHERE product = ?";
                int count = jdbcTemplate.queryForObject(checkSql, Integer.class, shipment.getProduct());
                
                if (count == 0) {
                    // 如果不存在，插入一条新记录
                    String insertSql = "INSERT INTO stock(product, unitstock, unitprice, inamount, outamount, lastoutdate, stockstatus) " +
                            "VALUES(?, 0, ?, 0, ?, ?, '1')";
                    jdbcTemplate.update(insertSql, shipment.getProduct(), shipment.getUnitprice(), shipment.getAmount(), shipment.getBilldate());
                } else {
                    // 如果存在，更新outamount和lastoutdate字段
                    String updateNewSql = "UPDATE stock SET outamount = outamount + ?, lastoutdate = ? WHERE product = ?";
                    jdbcTemplate.update(updateNewSql, shipment.getAmount(), shipment.getBilldate(), shipment.getProduct());
                }
            }
            
            // 提交事务
            jdbcTemplate.execute("COMMIT");
            return result;
        } catch (Exception e) {
            // 回滚事务
            jdbcTemplate.execute("ROLLBACK");
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int updatePaystatusShipmentById(int id) {
        String sql = "UPDATE shipment set paystatus = '1' WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public List<String> findCustomerNamesByPrefix(String prefix, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        String sql = "SELECT DISTINCT customer FROM shipment WHERE customer LIKE ? AND is_delete = 0 ORDER BY customer LIMIT ? OFFSET ?";
        return jdbcTemplate.queryForList(sql, new Object[]{"%" + prefix + "%", pageSize, offset}, String.class);
    }

    @Override
    public int getCustomerNamesCount(String prefix) {
        String sql = "SELECT COUNT(DISTINCT customer) FROM shipment WHERE customer LIKE ? AND is_delete = 0";
        return jdbcTemplate.queryForObject(sql, new Object[]{"%" + prefix + "%"}, Integer.class);
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
            shipment.setRemark(rs.getString("remark"));
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
