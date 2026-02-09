package com.xdmy.dao.inter.impl;


import com.xdmy.dao.inter.IIncomingDao;
import com.xdmy.domain.Incoming;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class IncomingDao extends BaseDao implements IIncomingDao {

    @Override
    public List<Incoming> findAllIncoming(int pageNum, int pageSize, String producerName, String productName, String bizStartDate, String bizEndDate) {
        int currOffset = (pageNum - 1) * pageSize;
        String sql = "SELECT * FROM incoming WHERE 1=1 AND is_delete = 0";
        sql = genFilterSql(sql, producerName, productName, bizStartDate, bizEndDate);
        sql += " ORDER BY billdate DESC,odd DESC,id DESC LIMIT ? ,?";
        return jdbcTemplate.query(sql, new Object[]{currOffset, pageSize}, new IncomingRowMapper());
    }

    @Override
    public List<Incoming> getIncomingStatement(String producerName, String bizStartDate, String bizEndDate) {
        String sql = "SELECT * FROM incoming WHERE 1=1 AND is_delete = 0 AND paystatus != 2" +
                " AND producer = ? AND billdate >= ? AND billdate <= ?";
        sql += " ORDER BY billdate,odd,id";
        return jdbcTemplate.query(sql, new Object[]{producerName, bizStartDate, bizEndDate}, new IncomingRowMapper());

    }

    @Override
    public int getDistinctSize(String producerName, String bizStartDate, String bizEndDate) {
        String sql = "SELECT count(distinct odd) FROM incoming WHERE 1=1 AND is_delete = 0 AND paystatus != 2" +
                " AND producer = ? AND billdate >= ? AND billdate <= ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, producerName, bizStartDate, bizEndDate);

    }

    @Override
    public double getSumPay(String producerName, String bizStartDate, String bizEndDate) {
        String sql = "SELECT sum(money) FROM incoming WHERE 1=1 AND is_delete = 0 AND paystatus != 2" +
                " AND producer = ? AND billdate >= ? AND billdate <= ?";
        return jdbcTemplate.queryForObject(sql, Double.class, producerName, bizStartDate, bizEndDate);
    }

    @Override
    public int getAllTotalSize(String producerName, String productName, String bizStartDate, String bizEndDate) {
        String sql = "SELECT count(1) FROM incoming WHERE 1=1 AND is_delete = 0";
        sql = genFilterSql(sql, producerName, productName, bizStartDate, bizEndDate);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public int addIncoming(Incoming incoming) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 添加入货记录
            String sql = "INSERT INTO incoming(odd,producer,product,billdate,amount,unitprice,money,paystatus,remark) " +
                    "VALUES(?,?,?,?,?,?,?,?,?)";
            int result = jdbcTemplate.update(sql, incoming.getOdd(), incoming.getProducer(), incoming.getProduct(), incoming.getBilldate(), incoming.getAmount(), incoming.getUnitprice(), incoming.getMoney(), incoming.getPaystatus(), incoming.getRemark());
            
            // 检查stock表中是否存在对应产品的记录
            String checkSql = "SELECT COUNT(*) FROM stock WHERE product = ?";
            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, incoming.getProduct());
            
            if (count == 0) {
                // 如果不存在，插入一条新记录
                String insertSql = "INSERT INTO stock(product, unitstock, unitprice, purchaseprice, inamount, outamount, lastindate, stockstatus) " +
                        "VALUES(?, 0, ?, ?, ?, 0, ?, '1')";
                jdbcTemplate.update(insertSql, incoming.getProduct(), incoming.getUnitprice(), incoming.getUnitprice(), incoming.getAmount(), incoming.getBilldate());
            } else {
                // 如果存在，更新inamount、purchaseprice和lastindate字段
                String updateSql = "UPDATE stock SET inamount = inamount + ?, purchaseprice = ?, lastindate = ? WHERE product = ?";
                jdbcTemplate.update(updateSql, incoming.getAmount(), incoming.getUnitprice(), incoming.getBilldate(), incoming.getProduct());
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
    public int deleteIncomingById(int id) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 先查询要删除的入货记录的产品和数量
            String querySql = "SELECT product, amount, billdate, unitprice FROM incoming WHERE id = ?";
            Incoming incoming = jdbcTemplate.queryForObject(querySql, new Object[]{id}, new IncomingRowMapper());
            
            // 删除入货记录
            String sql = "UPDATE incoming set is_delete = 1 WHERE id = ?";
            int result = jdbcTemplate.update(sql, id);
            
            // 更新stock表中对应产品的inamount和purchaseprice字段
            String updateSql = "UPDATE stock SET inamount = inamount - ?, " +
                    "purchaseprice = (SELECT unitprice FROM incoming WHERE product = ? AND is_delete = 0 ORDER BY billdate DESC LIMIT 1), " +
                    "lastindate = (SELECT MAX(billdate) FROM incoming WHERE product = ? AND is_delete = 0) " +
                    "WHERE product = ?";
            jdbcTemplate.update(updateSql, incoming.getAmount(), incoming.getProduct(), incoming.getProduct(), incoming.getProduct());
            
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
    public int updateIncoming(Incoming incoming) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 先查询原入货记录的产品和数量
            String querySql = "SELECT product, amount FROM incoming WHERE id = ?";
            Incoming oldIncoming = jdbcTemplate.queryForObject(querySql, new Object[]{incoming.getId()}, new IncomingRowMapper());
            
            // 更新入货记录
            String sql = "UPDATE incoming set odd = ?,producer = ?,product = ?,billdate = ?,amount = ?,unitprice = ?,money = ? " +
                    ",paystatus = ?,remark = ? " +
                    "WHERE id = ? ";
            int result = jdbcTemplate.update(sql, incoming.getOdd(), incoming.getProducer(), incoming.getProduct(), incoming.getBilldate(), incoming.getAmount(), incoming.getUnitprice(), incoming.getMoney(), incoming.getPaystatus(), incoming.getRemark(), incoming.getId());
            
            // 如果产品没有变化
            if (oldIncoming.getProduct().equals(incoming.getProduct())) {
                // 更新stock表中对应产品的inamount字段
                int amountDiff = incoming.getAmount() - oldIncoming.getAmount();
                String updateSql = "UPDATE stock SET inamount = inamount + ?, purchaseprice = ?, lastindate = ? WHERE product = ?";
                jdbcTemplate.update(updateSql, amountDiff, incoming.getUnitprice(), incoming.getBilldate(), incoming.getProduct());
            } else {
                // 如果产品变化了，需要更新两个产品的inamount字段
                // 减少原产品的inamount
                String updateOldSql = "UPDATE stock SET inamount = inamount - ?, " +
                        "purchaseprice = (SELECT unitprice FROM incoming WHERE product = ? AND is_delete = 0 ORDER BY billdate DESC LIMIT 1), " +
                        "lastindate = (SELECT MAX(billdate) FROM incoming WHERE product = ? AND is_delete = 0) " +
                        "WHERE product = ?";
                jdbcTemplate.update(updateOldSql, oldIncoming.getAmount(), oldIncoming.getProduct(), oldIncoming.getProduct(), oldIncoming.getProduct());
                
                // 增加新产品的inamount
                // 检查新产品是否在stock表中存在
                String checkSql = "SELECT COUNT(*) FROM stock WHERE product = ?";
                int count = jdbcTemplate.queryForObject(checkSql, Integer.class, incoming.getProduct());
                
                if (count == 0) {
                    // 如果不存在，插入一条新记录
                    String insertSql = "INSERT INTO stock(product, unitstock, unitprice, purchaseprice, inamount, outamount, lastindate, stockstatus) " +
                            "VALUES(?, 0, ?, ?, ?, 0, ?, '1')";
                    jdbcTemplate.update(insertSql, incoming.getProduct(), incoming.getUnitprice(), incoming.getUnitprice(), incoming.getAmount(), incoming.getBilldate());
                } else {
                    // 如果存在，更新inamount、purchaseprice和lastindate字段
                    String updateNewSql = "UPDATE stock SET inamount = inamount + ?, purchaseprice = ?, lastindate = ? WHERE product = ?";
                    jdbcTemplate.update(updateNewSql, incoming.getAmount(), incoming.getUnitprice(), incoming.getBilldate(), incoming.getProduct());
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
    public int updatePaystatusIncomingById(int id) {
        String sql = "UPDATE incoming set paystatus = '2' WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    static class IncomingRowMapper implements RowMapper<Incoming> {
        @Override
        public Incoming mapRow(ResultSet rs, int rowNum) throws SQLException {
            Incoming incoming = new Incoming();
            incoming.setId(rs.getInt("id"));
            incoming.setOdd(rs.getString("odd"));
            incoming.setProducer(rs.getString("producer"));
            incoming.setProduct(rs.getString("product"));
            incoming.setBilldate(rs.getString("billdate"));
            incoming.setAmount(rs.getInt("amount"));
            incoming.setUnitprice(rs.getDouble("unitprice"));
            incoming.setMoney(rs.getDouble("money"));
            incoming.setPaystatus(rs.getString("paystatus"));
            incoming.setRemark(rs.getString("remark"));
            return incoming;
        }
    }

    public String genFilterSql(String sql, String producerName, String productName, String bizStartDate, String bizEndDate) {
        if (!producerName.equals("")) {
            sql += " AND producer LIKE '%" + producerName + "%'";
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

    @Override
    public List<String> findProducerNamesByPrefix(String prefix, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        // 优先匹配包含完整前缀的供应商，然后匹配包含前缀中每个字符的供应商
        String sql = "SELECT DISTINCT producer FROM incoming " +
                     "WHERE is_delete = 0 AND (producer LIKE ? OR producer LIKE ?) " +
                     "ORDER BY " +
                     "CASE " +
                     "    WHEN producer LIKE ? THEN 0 " +
                     "    ELSE 1 " +
                     "END, " +
                     "producer " +
                     "LIMIT ? OFFSET ?";
        
        return jdbcTemplate.queryForList(sql, new Object[]{
            "%" + prefix + "%",  // 包含完整前缀
            "%" + prefix.replaceAll("", "%") + "%",  // 包含前缀中每个字符
            "%" + prefix + "%",  // 用于排序
            pageSize, 
            offset
        }, String.class);
    }

    @Override
    public int getProducerNamesCount(String prefix) {
        // 计算包含完整前缀或包含前缀中每个字符的供应商数量
        String sql = "SELECT COUNT(DISTINCT producer) FROM incoming " +
                     "WHERE is_delete = 0 AND (producer LIKE ? OR producer LIKE ?)";
        
        return jdbcTemplate.queryForObject(sql, new Object[]{
            "%" + prefix + "%",  // 包含完整前缀
            "%" + prefix.replaceAll("", "%") + "%"  // 包含前缀中每个字符
        }, Integer.class);
    }

}
