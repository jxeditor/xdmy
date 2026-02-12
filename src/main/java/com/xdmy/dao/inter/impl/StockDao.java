package com.xdmy.dao.inter.impl;


import com.xdmy.dao.inter.IStockDao;
import com.xdmy.domain.Stock;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StockDao extends BaseDao implements IStockDao {

    @Override
    public List<Stock> findAllStock(int pageNum, int pageSize, String productName, boolean hideZeroStock, String companyName) {
        int currOffset = (pageNum - 1) * pageSize;
        String sql = "SELECT id, product, unitstock, unitprice, purchaseprice, inamount, outamount, lastindate, lastoutdate, stockstatus " +
                "FROM stock " +
                "WHERE 1=1 AND company_name = ?";
        sql = genFilterSql(sql, productName);
        if (hideZeroStock) {
            sql += " AND unitstock != 0";
        }
        sql += " ORDER BY unitstock DESC LIMIT ? ,?";
        return jdbcTemplate.query(sql, new RowMapper<Stock>() {
            @Override
            public Stock mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                Stock stock = new Stock();
                stock.setId(rs.getInt("id"));
                stock.setProduct(rs.getString("product"));
                stock.setUnitstock(rs.getInt("unitstock"));
                stock.setUnitprice(rs.getDouble("unitprice"));
                stock.setPurchaseprice(rs.getDouble("purchaseprice"));
                stock.setInamount(rs.getInt("inamount"));
                stock.setOutamount(rs.getInt("outamount"));
                stock.setStock(rs.getInt("unitstock") + rs.getInt("inamount") - rs.getInt("outamount"));
                stock.setMoney(rs.getInt("unitstock") * rs.getDouble("unitprice"));
                stock.setLastindate(rs.getString("lastindate"));
                stock.setLastoutdate(rs.getString("lastoutdate"));
                stock.setStockstatus(rs.getString("stockstatus"));
                return stock;
            }
        }, companyName, currOffset, pageSize);
    }

    @Override
    public List<Stock> findSurplusStock(String companyName) {
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
                "  WHERE stockstatus != '2' AND company_name = ? " +
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
                "   WHERE is_delete = 0 AND company_name = ? " +
                "   GROUP BY product  " +
                "   UNION ALL " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'入货' source " +
                "   FROM incoming  " +
                "   WHERE is_delete = 0 AND company_name = ? " +
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
                "  WHERE stockstatus != '2' AND company_name = ? " +
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
                "   WHERE is_delete = 0 AND company_name = ? " +
                "   GROUP BY product  " +
                "   UNION ALL " +
                "   SELECT product,sum( amount ) amount,max(billdate) billdate,'入货' source " +
                "   FROM incoming  " +
                "   WHERE is_delete = 0 AND company_name = ? " +
                "   GROUP BY product  " +
                "  ) t1 " +
                "  GROUP BY product " +
                " ) t2 " +
                " ON t1.product = t2.product" +
                " WHERE t1.product is null" +
                ") t1 WHERE 1=1 " +
                "AND t1.stock <= 100 " +
                "ORDER BY product";
        return jdbcTemplate.query(sql, new StockRowMapper(), companyName, companyName, companyName, companyName, companyName, companyName);
    }

    @Override
    public int getAllTotalSize(String productName, boolean hideZeroStock, String companyName) {
        String sql = "SELECT count(1) " +
                "FROM stock " +
                "WHERE 1=1 AND company_name = ?";
        sql = genFilterSql(sql, productName);
        if (hideZeroStock) {
            sql += " AND unitstock != 0";
        }
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, companyName);
        return count != null ? count : 0;
    }

    @Override
    public int flattenStock(String companyName) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 第一步：初始化UID为0的数据
            String initSql = "INSERT INTO stock(product, unitstock, unitprice, purchaseprice, inamount, outamount, lastindate, lastoutdate, stockstatus, company_name) " +
                    "SELECT DISTINCT product, 0, " +
                    "(SELECT unitprice FROM incoming WHERE product = t1.product AND company_name = ? ORDER BY billdate DESC LIMIT 1), " +
                    "(SELECT unitprice FROM incoming WHERE product = t1.product AND company_name = ? ORDER BY billdate DESC LIMIT 1), " +
                    "(SELECT COALESCE(SUM(amount), 0) FROM incoming WHERE product = t1.product AND is_delete = 0 AND company_name = ?), " +
                    "(SELECT COALESCE(SUM(amount), 0) FROM shipment WHERE product = t1.product AND is_delete = 0 AND company_name = ?), " +
                    "(SELECT MAX(billdate) FROM incoming WHERE product = t1.product AND is_delete = 0 AND company_name = ?), " +
                    "(SELECT MAX(billdate) FROM shipment WHERE product = t1.product AND is_delete = 0 AND company_name = ?), " +
                    "'1', ? " +
                    "FROM ( " +
                    "SELECT product FROM shipment WHERE product NOT IN (SELECT product FROM stock WHERE company_name = ?) AND company_name = ? " +
                    "UNION " +
                    "SELECT product FROM incoming WHERE product NOT IN (SELECT product FROM stock WHERE company_name = ?) AND company_name = ? " +
                    ") t1 " +
                    "WHERE EXISTS ( " +
                    "  SELECT 1 FROM ( " +
                    "    SELECT product FROM shipment WHERE product = t1.product AND company_name = ? " +
                    "    UNION " +
                    "    SELECT product FROM incoming WHERE product = t1.product AND company_name = ? " +
                    "  ) t2 " +
                    ")";
            int initCount = jdbcTemplate.update(initSql, companyName, companyName, companyName, companyName, companyName, companyName, companyName, companyName, companyName, companyName, companyName, companyName);

            // 第二步：调整负库存为0
            String adjustSql = "UPDATE stock " +
                    "SET unitstock = ABS((inamount - outamount)), stockstatus = '1' " +
                    "WHERE (unitstock + (inamount - outamount)) < 0 AND company_name = ?";
            int adjustCount = jdbcTemplate.update(adjustSql, companyName);

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
    public int getFlattenStockCount(String companyName) {
        // 计算需要初始化的UID为0的数据数量
        String initCountSql = "SELECT COUNT(DISTINCT product) " +
                "FROM ( " +
                "  SELECT product FROM shipment WHERE product NOT IN (SELECT product FROM stock WHERE company_name = ?) AND company_name = ? " +
                "  UNION " +
                "  SELECT product FROM incoming WHERE product NOT IN (SELECT product FROM stock WHERE company_name = ?) AND company_name = ? " +
                ") t1 " +
                "WHERE EXISTS ( " +
                "  SELECT 1 FROM ( " +
                "    SELECT product FROM shipment WHERE product = t1.product AND company_name = ? " +
                "    UNION " +
                "    SELECT product FROM incoming WHERE product = t1.product AND company_name = ? " +
                "  ) t2 " +
                ")";
        Integer initCount = jdbcTemplate.queryForObject(initCountSql, Integer.class, companyName, companyName, companyName, companyName, companyName, companyName);

        // 计算需要调整的负库存数据数量
        String adjustCountSql = "SELECT COUNT(*) " +
                "FROM stock " +
                "WHERE unitstock < 0 AND company_name = ?";
        Integer adjustCount = jdbcTemplate.queryForObject(adjustCountSql, Integer.class, companyName);

        // 返回总数量
        int init = initCount != null ? initCount : 0;
        int adjust = adjustCount != null ? adjustCount : 0;
        return init + adjust;
    }

    @Override
    public int addStock(Stock stock) {
        // 检查product表中是否存在对应产品的记录
        String checkProductSql = "SELECT COUNT(*) FROM product WHERE product_name = ?";
        Integer productCount = jdbcTemplate.queryForObject(checkProductSql, Integer.class, stock.getProduct());
        productCount = productCount != null ? productCount : 0;
        
        if (productCount == 0) {
            // 如果不存在，插入一条新记录到product表
            String insertProductSql = "INSERT INTO product(product_name, suggested_price, cost_price, maintain_material) " +
                    "VALUES(?, ?, ?, 0)";
            jdbcTemplate.update(insertProductSql, stock.getProduct(), stock.getUnitprice(), stock.getPurchaseprice());
        }
        
        // 添加库存
        String sql = "INSERT INTO stock(product,unitstock,unitprice,purchaseprice,inamount,outamount,lastindate,lastoutdate,stockstatus,company_name) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, stock.getProduct(), stock.getUnitstock(), stock.getUnitprice(), stock.getPurchaseprice(), stock.getInamount(), stock.getOutamount(), stock.getLastindate(), stock.getLastoutdate(), "1", stock.getCompany_name() != null ? stock.getCompany_name() : "");
    }

    @Override
    public int deleteStockById(int id) {
        String sql = "DELETE FROM stock WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int batchDeleteStock(String ids) {
        String sql = "DELETE FROM stock WHERE id IN (" + ids + ")";
        return jdbcTemplate.update(sql);
    }

    @Override
    public int updateStock(Stock stock) {
        String sql = "UPDATE stock set product = ?,unitstock = ?,unitprice = ?,purchaseprice = ?,inamount = ?,outamount = ?,lastindate = ?,lastoutdate = ?,stockstatus = ? " +
                "WHERE id = ? ";
        return jdbcTemplate.update(sql, stock.getProduct(), stock.getUnitstock(), stock.getUnitprice(), stock.getPurchaseprice(), stock.getInamount(), stock.getOutamount(), stock.getLastindate(), stock.getLastoutdate(), stock.getStockstatus(), stock.getId());
    }

    @Override
    public List<String> findProductNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName) {
        int offset = (pageNum - 1) * pageSize;
        // 优先匹配包含完整前缀的产品，然后匹配包含前缀中每个字符的产品
        String sql = "SELECT DISTINCT product FROM stock " +
                     "WHERE company_name = ? AND (product LIKE ? OR product LIKE ?) " +
                     "ORDER BY " +
                     "CASE " +
                     "    WHEN product LIKE ? THEN 0 " +
                     "    ELSE 1 " +
                     "END, " +
                     "product " +
                     "LIMIT ? OFFSET ?";
        
        return jdbcTemplate.queryForList(sql, String.class, 
            companyName,
            "%" + prefix + "%",  // 包含完整前缀
            "%" + prefix.replaceAll("", "%") + "%",  // 包含前缀中每个字符
            "%" + prefix + "%",  // 用于排序
            pageSize, 
            offset
        );
    }

    @Override
    public int getProductNamesCount(String prefix, String companyName) {
        // 计算包含完整前缀或包含前缀中每个字符的产品数量
        String sql = "SELECT COUNT(DISTINCT product) FROM stock " +
                     "WHERE company_name = ? AND (product LIKE ? OR product LIKE ?)";
        
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, 
            companyName,
            "%" + prefix + "%",  // 包含完整前缀
            "%" + prefix.replaceAll("", "%") + "%"  // 包含前缀中每个字符
        );
        return count != null ? count : 0;
    }

    static class StockRowMapper implements RowMapper<Stock> {
        @Override
        public Stock mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
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

    public @NonNull String genFilterSql(@NonNull String sql, String productName) {
        if (!productName.equals("")) {
            sql += " AND product like '%" + productName + "%'";
        }
        return sql;
    }
}