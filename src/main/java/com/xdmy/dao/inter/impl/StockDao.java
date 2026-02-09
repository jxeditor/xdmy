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
    public List<Stock> findAllStock(int pageNum, int pageSize, String productName, boolean hideZeroStock) {
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
        if (hideZeroStock) {
            sql += " AND t1.stock != 0";
        }
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
    public int getAllTotalSize(String productName, boolean hideZeroStock) {
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
                "   FROM incoming " +
                "   WHERE is_delete = 0 " +
                "   GROUP BY product  " +
                "  ) t1 " +
                "  GROUP BY product " +
                " ) t2 " +
                " ON t1.product = t2.product " +
                " WHERE t1.product is null" +
                ") t1 WHERE 1=1";
        sql = genFilterSql(sql, productName);
        if (hideZeroStock) {
            sql += " AND t1.stock != 0";
        }
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public int flattenStock() {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 第一步：初始化UID为0的数据
            String initSql = "INSERT INTO stock(product, unitstock, unitprice, stockstatus) " +
                    "SELECT DISTINCT product, 0, (SELECT unitprice FROM incoming WHERE product = t1.product ORDER BY billdate DESC LIMIT 1), '1' " +
                    "FROM ( " +
                    "  SELECT product FROM shipment WHERE product NOT IN (SELECT product FROM stock) " +
                    "  UNION " +
                    "  SELECT product FROM incoming WHERE product NOT IN (SELECT product FROM stock) " +
                    ") t1 " +
                    "WHERE EXISTS ( " +
                    "  SELECT 1 FROM ( " +
                    "    SELECT product FROM shipment WHERE product = t1.product " +
                    "    UNION " +
                    "    SELECT product FROM incoming WHERE product = t1.product " +
                    "  ) t2 " +
                    ")";
            int initCount = jdbcTemplate.update(initSql);

            // 第二步：调整负库存为0
            String adjustSql = "UPDATE stock s " +
                    "JOIN ( " +
                    "  SELECT product, COALESCE(SUM(CASE WHEN source = '入货' THEN amount ELSE -amount END), 0) as balance " +
                    "  FROM ( " +
                    "    SELECT product, amount, '出货' as source FROM shipment WHERE is_delete = 0 " +
                    "    UNION ALL " +
                    "    SELECT product, amount, '入货' as source FROM incoming WHERE is_delete = 0 " +
                    "  ) t " +
                    "  GROUP BY product " +
                    ") t ON s.product = t.product " +
                    "SET s.unitstock = ABS(t.balance), s.stockstatus = '1' " +
                    "WHERE (s.unitstock + t.balance) < 0";
            int adjustCount = jdbcTemplate.update(adjustSql);

            // 提交事务
            jdbcTemplate.execute("COMMIT");
            return initCount + adjustCount;
        } catch (Exception e) {
            // 回滚事务
            jdbcTemplate.execute("ROLLBACK");
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getFlattenStockCount() {
        // 计算需要初始化的UID为0的数据数量
        String initCountSql = "SELECT COUNT(DISTINCT product) " +
                "FROM ( " +
                "  SELECT product FROM shipment WHERE product NOT IN (SELECT product FROM stock) " +
                "  UNION " +
                "  SELECT product FROM incoming WHERE product NOT IN (SELECT product FROM stock) " +
                ") t1 " +
                "WHERE EXISTS ( " +
                "  SELECT 1 FROM ( " +
                "    SELECT product FROM shipment WHERE product = t1.product " +
                "    UNION " +
                "    SELECT product FROM incoming WHERE product = t1.product " +
                "  ) t2 " +
                ")";
        Integer initCount = jdbcTemplate.queryForObject(initCountSql, Integer.class);

        // 计算需要调整的负库存数据数量
        String adjustCountSql = "SELECT COUNT(*) " +
                "FROM stock s " +
                "JOIN ( " +
                "  SELECT product, COALESCE(SUM(CASE WHEN source = '入货' THEN amount ELSE -amount END), 0) as balance " +
                "  FROM ( " +
                "    SELECT product, amount, '出货' as source FROM shipment WHERE is_delete = 0 " +
                "    UNION ALL " +
                "    SELECT product, amount, '入货' as source FROM incoming WHERE is_delete = 0 " +
                "  ) t " +
                "  GROUP BY product " +
                ") t ON s.product = t.product " +
                "WHERE (s.unitstock + t.balance) < 0";
        Integer adjustCount = jdbcTemplate.queryForObject(adjustCountSql, Integer.class);

        // 返回总数量
        return initCount + adjustCount;
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

    @Override
    public List<String> findProductNamesByPrefix(String prefix, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        // 优先匹配包含完整前缀的产品，然后匹配包含前缀中每个字符的产品
        String sql = "SELECT DISTINCT product FROM stock " +
                     "WHERE product LIKE ? OR product LIKE ? " +
                     "ORDER BY " +
                     "CASE " +
                     "    WHEN product LIKE ? THEN 0 " +
                     "    ELSE 1 " +
                     "END, " +
                     "product " +
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
    public int getProductNamesCount(String prefix) {
        // 计算包含完整前缀或包含前缀中每个字符的产品数量
        String sql = "SELECT COUNT(DISTINCT product) FROM stock " +
                     "WHERE product LIKE ? OR product LIKE ?";
        
        return jdbcTemplate.queryForObject(sql, new Object[]{
            "%" + prefix + "%",  // 包含完整前缀
            "%" + prefix.replaceAll("", "%") + "%"  // 包含前缀中每个字符
        }, Integer.class);
    }

    static class StockRowMapper implements RowMapper<Stock> {
        @Override
        public Stock mapRow(ResultSet rs, int rowNum) throws SQLException {
            Stock stock = new Stock();
            stock.setId(rs.getInt("id"));
            stock.setProduct(rs.getString("product"));
            stock.setUnitstock(rs.getInt("unitstock"));
            stock.setUnitprice(rs.getDouble("unitprice"));
            stock.setPurchaseprice(rs.getDouble("purchaseprice"));
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
