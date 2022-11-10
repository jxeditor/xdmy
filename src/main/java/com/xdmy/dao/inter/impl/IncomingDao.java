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
        String sql = "INSERT INTO incoming(odd,producer,product,billdate,amount,unitprice,money,paystatus,remark) " +
                "VALUES(?,?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, incoming.getOdd(), incoming.getProducer(), incoming.getProduct(), incoming.getBilldate(), incoming.getAmount(), incoming.getUnitprice(), incoming.getMoney(), incoming.getPaystatus(), incoming.getRemark());
    }

    @Override
    public int deleteIncomingById(int id) {
        String sql = "UPDATE incoming set is_delete = 1 WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int updateIncoming(Incoming incoming) {
        String sql = "UPDATE incoming set odd = ?,producer = ?,product = ?,billdate = ?,amount = ?,unitprice = ?,money = ? " +
                ",paystatus = ?,remark = ? " +
                "WHERE id = ? ";
        return jdbcTemplate.update(sql, incoming.getOdd(), incoming.getProducer(), incoming.getProduct(), incoming.getBilldate(), incoming.getAmount(), incoming.getUnitprice(), incoming.getMoney(), incoming.getPaystatus(), incoming.getRemark(), incoming.getId());
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

}
