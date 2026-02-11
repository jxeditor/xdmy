package com.xdmy.dao.inter.impl;

import com.xdmy.dao.inter.ITurnoverDao;
import com.xdmy.domain.Turnover;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TurnoverDao extends BaseDao implements ITurnoverDao {

    @Override
    public List<Turnover> findAllTurnover(int pageNum, int pageSize, String payerName, String payeeName,
            String bizStartDate, String bizEndDate) {
        int currOffset = (pageNum - 1) * pageSize;
        String sql = "SELECT * FROM turnover WHERE 1=1 AND is_delete = 0";
        sql = genFilterSql(sql, payerName, payeeName, bizStartDate, bizEndDate);
        sql += " ORDER BY billdate DESC,create_time DESC LIMIT ? ,?";
        return jdbcTemplate.query(sql, new TurnoverRowMapper(), currOffset, pageSize);
    }

    @Override
    public int getAllTotalSize(String payerName, String payeeName, String bizStartDate, String bizEndDate) {
        String sql = "SELECT count(1) FROM turnover WHERE 1=1 AND is_delete = 0";
        sql = genFilterSql(sql, payerName, payeeName, bizStartDate, bizEndDate);
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public int addTurnover(Turnover turnover) {
        String sql = "INSERT INTO turnover(payer,payee,billdate,money,tax,paid,remark) " +
                "VALUES(?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, turnover.getPayer(), turnover.getPayee(), turnover.getBilldate(),
                turnover.getMoney(), turnover.getTax(), turnover.getPaid(), turnover.getRemark());
    }

    @Override
    public int deleteTurnoverById(int id) {
        String sql = "UPDATE turnover set is_delete = 1 WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int updateTurnover(Turnover turnover) {
        String sql = "UPDATE turnover set payer = ?,payee = ?,billdate = ?,money = ?,tax = ?,paid = ?" +
                ",remark = ? " +
                "WHERE id = ? ";
        return jdbcTemplate.update(sql, turnover.getPayer(), turnover.getPayee(), turnover.getBilldate(),
                turnover.getMoney(), turnover.getTax(), turnover.getPaid(), turnover.getRemark(), turnover.getId());
    }

    static class TurnoverRowMapper implements RowMapper<Turnover> {
        @Override
        public Turnover mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Turnover turnover = new Turnover();
            turnover.setId(rs.getInt("id"));
            turnover.setPayer(rs.getString("payer"));
            turnover.setPayee(rs.getString("payee"));
            turnover.setBilldate(rs.getString("billdate"));
            turnover.setMoney(rs.getDouble("money"));
            turnover.setTax(rs.getDouble("tax"));
            turnover.setPaid(rs.getDouble("paid"));
            turnover.setRemark(rs.getString("remark"));
            return turnover;
        }
    }

    public @NonNull String genFilterSql(@NonNull String sql, String payerName, String payeeName, String bizStartDate,
            String bizEndDate) {
        if (payerName != null && !payerName.equals("")) {
            sql += " AND payer LIKE '%" + payerName + "%'";
        }
        if (payeeName != null && !payeeName.equals("")) {
            sql += " AND payee LIKE '%" + payeeName + "%'";
        }
        if (bizStartDate != null && !bizStartDate.equals("undefined")) {
            sql += " AND billdate >= '" + bizStartDate + "'";
        }
        if (bizEndDate != null && !bizEndDate.equals("undefined")) {
            sql += " AND billdate <= '" + bizEndDate + "'";
        }
        return sql;
    }

}
