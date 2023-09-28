package com.xdmy.dao.inter.impl;


import com.xdmy.dao.inter.IStockDao;
import com.xdmy.domain.Stock;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StockDao extends BaseDao implements IStockDao {

    @Override
    public List<Stock> findAllStock(int pageNum, int pageSize, String productName) {
        int currOffset = (pageNum - 1) * pageSize;
        String sql = "SELECT * " +
                "FROM ( " +
                " SELECT t1.id " +
                "  ,COALESCE(t1.product,t2.product) product " +
                "  ,t1.unitstock " +
                "  ,t1.unitprice unitprice " +
                "  ,t3.unitprice purchaseprice " +
                "  ,t2.inamount  " +
                "  ,t2.outamount " +
                "  ,COALESCE(t1.unitstock,0) + COALESCE(t2.amount,0) stock " +
                "  ,if(t1.unitprice is not null,(COALESCE(t1.unitstock,0) + COALESCE(t2.amount,0))*t1.unitprice,null) money " +
                "  ,t2.lastindate " +
                "  ,t2.lastoutdate " +
                "  ,if(t1.unitprice is not null,t1.stockstatus,'0') stockstatus " +
                " FROM ( " +
                "  SELECT *  " +
                "  FROM stock " +
                " ) t1 " +
                " LEFT JOIN ( " +
                "  SELECT product " +
                "   ,sum(if(source='入货',amount,0)) inamount " +
                "   ,sum(if(source='出货',amount,0)) outamount " +
                "   ,max(if(source='入货',billdate,null)) lastindate " +
                "   ,max(if(source='出货',billdate,null)) lastoutdate " +
                "   ,sum(if(source='入货',amount,0)) - sum(if(source='出货',amount,0)) amount " +
                "  FROM ( " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'出货' source " +
                "   FROM shipment  " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "   UNION ALL " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'入货' source " +
                "   FROM incoming  " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "  ) t1 " +
                "  GROUP BY product " +
                " ) t2 " +
                " ON t1.product = t2.product " +
                " LEFT JOIN ( " +
                "   SELECT product,unitprice " +
                "   FROM ( " +
                "       SELECT product,unitprice,ROW_NUMBER() OVER(PARTITION BY product ORDER BY billdate DESC) rk " +
                "       FROM incoming " +
                "   ) t1 " +
                "   WHERE rk = 1 " +
                " ) t3 " +
                " ON t1.product = t3.product  " +
                " UNION ALL " +
                " SELECT t1.id " +
                "  ,COALESCE(t1.product,t2.product) product " +
                "  ,t1.unitstock " +
                "  ,t1.unitprice unitprice " +
                "  ,t3.unitprice purchaseprice " +
                "  ,t2.inamount  " +
                "  ,t2.outamount " +
                "  ,COALESCE(t1.unitstock,0) + t2.amount stock " +
                "  ,if(t1.unitprice is not null,(COALESCE(t1.unitstock,0) + t2.amount)*t1.unitprice,null) money " +
                "  ,t2.lastindate " +
                "  ,t2.lastoutdate " +
                "  ,if(t1.unitprice is not null,t1.stockstatus,'0') stockstatus " +
                " FROM ( " +
                "  SELECT * " +
                "  FROM stock " +
                " ) t1 " +
                " RIGHT JOIN ( " +
                "  SELECT product " +
                "   ,sum(if(source='入货',amount,0)) inamount " +
                "   ,sum(if(source='出货',amount,0)) outamount " +
                "   ,max(if(source='入货',billdate,null)) lastindate " +
                "   ,max(if(source='出货',billdate,null)) lastoutdate " +
                "   ,sum(if(source='入货',amount,0)) - sum(if(source='出货',amount,0)) amount " +
                "  FROM ( " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'出货' source " +
                "   FROM shipment  " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "   UNION ALL " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'入货' source " +
                "   FROM incoming  " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "  ) t1 " +
                "  GROUP BY product " +
                " ) t2 " +
                " ON t1.product = t2.product " +
                " LEFT JOIN ( " +
                "   SELECT product,unitprice " +
                "   FROM ( " +
                "       SELECT product,unitprice,ROW_NUMBER() OVER(PARTITION BY product ORDER BY billdate DESC) rk " +
                "       FROM incoming " +
                "   ) t1 " +
                "   WHERE rk = 1 " +
                " ) t3 " +
                " ON t2.product = t3.product  " +
                " WHERE t1.product is null" +
                ") t1 WHERE 1=1";
        sql = genFilterSql(sql, productName);
        sql += " ORDER BY stock DESC LIMIT ? ,?";
        return jdbcTemplate.query(sql, new Object[]{currOffset, pageSize}, new StockRowMapper());
    }

    @Override
    public List<Stock> findSurplusStock() {
        String sql = "SELECT * " +
                "FROM ( " +
                " SELECT t1.id " +
                "  ,COALESCE(t1.product,t2.product) product " +
                "  ,t1.unitstock " +
                "  ,t1.unitprice " +
                "  ,t2.inamount  " +
                "  ,t2.outamount " +
                "  ,COALESCE(t1.unitstock,0) + COALESCE(t2.amount,0) stock " +
                "  ,if(t1.unitprice is not null,(COALESCE(t1.unitstock,0) + COALESCE(t2.amount,0))*t1.unitprice,null) money " +
                "  ,t2.lastindate " +
                "  ,t2.lastoutdate " +
                "  ,if(t1.unitprice is not null,t1.stockstatus,'0') stockstatus " +
                " FROM ( " +
                "  SELECT *  " +
                "  FROM stock " +
                "  WHERE stockstatus != '2' " +
                " ) t1 " +
                " LEFT JOIN ( " +
                "  SELECT product " +
                "   ,sum(if(source='入货',amount,0)) inamount " +
                "   ,sum(if(source='出货',amount,0)) outamount " +
                "   ,max(if(source='入货',billdate,null)) lastindate " +
                "   ,max(if(source='出货',billdate,null)) lastoutdate " +
                "   ,sum(if(source='入货',amount,0)) - sum(if(source='出货',amount,0)) amount " +
                "  FROM ( " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'出货' source " +
                "   FROM shipment  " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "   UNION ALL " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'入货' source " +
                "   FROM incoming  " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "  ) t1 " +
                "  GROUP BY product " +
                " ) t2 " +
                " ON t1.product = t2.product " +
                " UNION ALL " +
                " SELECT t1.id " +
                "  ,COALESCE(t1.product,t2.product) product " +
                "  ,t1.unitstock " +
                "  ,t1.unitprice " +
                "  ,t2.inamount  " +
                "  ,t2.outamount " +
                "  ,COALESCE(t1.unitstock,0) + t2.amount stock " +
                "  ,if(t1.unitprice is not null,(COALESCE(t1.unitstock,0) + t2.amount)*t1.unitprice,null) money " +
                "  ,t2.lastindate " +
                "  ,t2.lastoutdate " +
                "  ,if(t1.unitprice is not null,t1.stockstatus,'0') stockstatus " +
                " FROM ( " +
                "  SELECT * " +
                "  FROM stock " +
                "  WHERE stockstatus != '2' " +
                " ) t1 " +
                " RIGHT JOIN ( " +
                "  SELECT product " +
                "   ,sum(if(source='入货',amount,0)) inamount " +
                "   ,sum(if(source='出货',amount,0)) outamount " +
                "   ,max(if(source='入货',billdate,null)) lastindate " +
                "   ,max(if(source='出货',billdate,null)) lastoutdate " +
                "   ,sum(if(source='入货',amount,0)) - sum(if(source='出货',amount,0)) amount " +
                "  FROM ( " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'出货' source " +
                "   FROM shipment  " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "   UNION ALL " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'入货' source " +
                "   FROM incoming  " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "  ) t1 " +
                "  GROUP BY product " +
                " ) t2 " +
                " ON t1.product = t2.product" +
                " WHERE t1.product is null" +
                ") t1 WHERE 1=1 " +
                "AND t1.stock <= 100 " +
                "ORDER BY product";
        return jdbcTemplate.query(sql, new StockRowMapper());
    }


    @Override
    public int getAllTotalSize(String productName) {
        String sql = "SELECT count(1) " +
                "FROM ( " +
                " SELECT t1.id " +
                "  ,COALESCE(t1.product,t2.product) product " +
                "  ,t1.unitstock " +
                "  ,t1.unitprice " +
                "  ,t2.inamount  " +
                "  ,t2.outamount " +
                "  ,COALESCE(t1.unitstock,0) + COALESCE(t2.amount,0) stock " +
                "  ,if(t1.unitprice is not null,(COALESCE(t1.unitstock,0) + COALESCE(t2.amount,0))*t1.unitprice,null) money " +
                "  ,t2.lastindate " +
                "  ,t2.lastoutdate " +
                "  ,if(t1.unitprice is not null,t1.stockstatus,'0') stockstatus " +
                " FROM ( " +
                "  SELECT *  " +
                "  FROM stock " +
                " ) t1 " +
                " LEFT JOIN ( " +
                "  SELECT product " +
                "   ,sum(if(source='入货',amount,0)) inamount " +
                "   ,sum(if(source='出货',amount,0)) outamount " +
                "   ,max(if(source='入货',billdate,null)) lastindate " +
                "   ,max(if(source='出货',billdate,null)) lastoutdate " +
                "   ,sum(if(source='入货',amount,0)) - sum(if(source='出货',amount,0)) amount " +
                "  FROM ( " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'出货' source " +
                "   FROM shipment  " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "   UNION ALL " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'入货' source " +
                "   FROM incoming  " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "  ) t1 " +
                "  GROUP BY product " +
                " ) t2 " +
                " ON t1.product = t2.product " +
                " UNION ALL " +
                " SELECT t1.id " +
                "  ,COALESCE(t1.product,t2.product) product " +
                "  ,t1.unitstock " +
                "  ,t1.unitprice " +
                "  ,t2.inamount  " +
                "  ,t2.outamount " +
                "  ,COALESCE(t1.unitstock,0) + t2.amount stock " +
                "  ,if(t1.unitprice is not null,(COALESCE(t1.unitstock,0) + t2.amount)*t1.unitprice,null) money " +
                "  ,t2.lastindate " +
                "  ,t2.lastoutdate " +
                "  ,if(t1.unitprice is not null,t1.stockstatus,'0') stockstatus " +
                " FROM ( " +
                "  SELECT * " +
                "  FROM stock " +
                " ) t1 " +
                " RIGHT JOIN ( " +
                "  SELECT product " +
                "   ,sum(if(source='入货',amount,0)) inamount " +
                "   ,sum(if(source='出货',amount,0)) outamount " +
                "   ,max(if(source='入货',billdate,null)) lastindate " +
                "   ,max(if(source='出货',billdate,null)) lastoutdate " +
                "   ,sum(if(source='入货',amount,0)) - sum(if(source='出货',amount,0)) amount " +
                "  FROM ( " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'出货' source " +
                "   FROM shipment  " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "   UNION ALL " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'入货' source " +
                "   FROM incoming" +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "  ) t1 " +
                "  GROUP BY product " +
                " ) t2 " +
                " ON t1.product = t2.product" +
                " WHERE t1.product is null" +
                ") t1 WHERE 1=1";
        sql = genFilterSql(sql, productName);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public int addStock(Stock stock) {
        String sql = "INSERT INTO stock(product,unitstock,unitprice,stockstatus) " +
                "VALUES(?,?,?,?)";
        return jdbcTemplate.update(sql, stock.getProduct(), stock.getUnitstock(), stock.getUnitprice(), "1");
    }

    @Override
    public int deleteStockById(int id) {
        String sql = "DELETE FROM stock WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int updateStock(Stock stock) {
        String sql = "UPDATE stock set product = ?,unitstock = ?,unitprice = ?,stockstatus = ?" +
                " WHERE id = ? ";
        return jdbcTemplate.update(sql, stock.getProduct(), stock.getUnitstock(), stock.getUnitprice(), stock.getStockstatus(), stock.getId());
    }

    static class StockRowMapper implements RowMapper<Stock> {
        @Override
        public Stock mapRow(ResultSet rs, int rowNum) throws SQLException {
            Stock stock = new Stock();
            stock.setId(rs.getInt("id"));
            stock.setProduct(rs.getString("product"));
            stock.setUnitstock(rs.getInt("unitstock"));
            stock.setUnitprice(rs.getDouble("unitprice"));
            stock.setInamount(rs.getInt("inamount"));
            stock.setOutamount(rs.getInt("outamount"));
            stock.setStock(rs.getInt("stock"));
            stock.setMoney(rs.getDouble("money"));
            stock.setLastindate(rs.getString("lastindate"));
            stock.setLastoutdate(rs.getString("lastoutdate"));
            stock.setStockstatus(rs.getString("stockstatus"));
            return stock;
        }
    }

    public String genFilterSql(String sql, String productName) {
        if (!productName.equals("")) {
            sql += " AND product like '%" + productName + "%'";
        }
        return sql;
    }

}
