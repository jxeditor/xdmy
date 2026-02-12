package com.xdmy.dao.inter.impl;

import com.xdmy.dao.inter.IProductDao;
import com.xdmy.domain.Product;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author xz
 * @Date 2026/2/10 12:00
 * @Description 产品DAO实现类
 */
@Repository
public class ProductDao extends BaseDao implements IProductDao {

    @Override
    public List<Product> findAllProduct(int pageNum, int pageSize, String productName, String companyName) {
        int currOffset = (pageNum - 1) * pageSize;
        String sql = "SELECT id, product_name, suggested_price, cost_price, maintain_material, create_time, update_time " +
                "FROM product " +
                "WHERE 1=1";
        sql = genFilterSql(sql, productName,companyName);
        sql += " ORDER BY product_name ASC LIMIT ? ,?";
        return jdbcTemplate.query(sql, new RowMapper<Product>() {
            @Override
            public Product mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setProductName(rs.getString("product_name"));
                product.setSuggestedPrice(rs.getDouble("suggested_price"));
                product.setCostPrice(rs.getDouble("cost_price"));
                product.setMaintainMaterial(rs.getInt("maintain_material"));
                product.setCreateTime(rs.getString("create_time"));
                product.setUpdateTime(rs.getString("update_time"));
                return product;
            }
        }, currOffset, pageSize);
    }

    @Override
    public int getAllTotalSize(String productName, String companyName) {
        String sql = "SELECT count(1) " +
                "FROM product " +
                "WHERE 1=1";
        sql = genFilterSql(sql, productName,companyName);
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public int addProduct(Product product, String companyName) {
        String sql = "INSERT INTO product(product_name, suggested_price, cost_price, maintain_material, company_name) " +
                "VALUES(?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, product.getProductName(), product.getSuggestedPrice(), product.getCostPrice(), product.getMaintainMaterial(), companyName);
    }

    @Override
    public int updateProduct(Product product, String companyName) {
        String sql = "UPDATE product SET product_name = ?, suggested_price = ?, cost_price = ?, maintain_material = ? " +
                "WHERE id = ? AND company_name = ?";
        return jdbcTemplate.update(sql, product.getProductName(), product.getSuggestedPrice(), product.getCostPrice(), product.getMaintainMaterial(), product.getId(), companyName);
    }

    @Override
    public int deleteProductById(int id) {
        String sql = "DELETE FROM product WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int batchDeleteProduct(String ids) {
        String sql = "DELETE FROM product WHERE id IN (" + ids + ")";
        return jdbcTemplate.update(sql);
    }

    @Override
    public Product findProductById(int id) {
        String sql = "SELECT id, product_name, suggested_price, cost_price, maintain_material, create_time, update_time " +
                "FROM product " +
                "WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new RowMapper<Product>() {
            @Override
            public Product mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setProductName(rs.getString("product_name"));
                product.setSuggestedPrice(rs.getDouble("suggested_price"));
                product.setCostPrice(rs.getDouble("cost_price"));
                product.setMaintainMaterial(rs.getInt("maintain_material"));
                product.setCreateTime(rs.getString("create_time"));
                product.setUpdateTime(rs.getString("update_time"));
                return product;
            }
        }, id);
    }

    @Override
    public List<String> findProductNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName) {
        int offset = (pageNum - 1) * pageSize;
        // 优先匹配包含完整前缀的产品，然后匹配包含前缀中每个字符的产品
        String sql = "SELECT DISTINCT product_name FROM product " +
                     "WHERE (product_name LIKE ? OR product_name LIKE ?) AND company_name = ? " +
                     "ORDER BY " +
                     "CASE " +
                     "    WHEN product_name LIKE ? THEN 0 " +
                     "    ELSE 1 " +
                     "END, " +
                     "product_name " +
                     "LIMIT ? OFFSET ?";
        
        return jdbcTemplate.queryForList(sql, String.class, 
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
        String sql = "SELECT COUNT(DISTINCT product_name) FROM product " +
                     "WHERE (product_name LIKE ? OR product_name LIKE ?) AND company_name = ?";
        
        return jdbcTemplate.queryForObject(sql, Integer.class, 
            "%" + prefix + "%",  // 包含完整前缀
            "%" + prefix.replaceAll("", "%") + "%",  // 包含前缀中每个字符
            companyName
        );
    }

    @Override
    public boolean checkProductExist(String productName, String companyName) {
        String sql = "SELECT COUNT(*) FROM product WHERE product_name = ? AND company_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, productName, companyName);
         return count != null && count > 0;
    }

    private String genFilterSql(String sql, String productName, String companyName) {
        if (!productName.equals("")) {
            sql += " AND product_name like '%" + productName + "%'";
        }
        if (!companyName.equals("")) {
            sql += " AND company_name = '" + companyName + "'";
        }
        return sql;
    }
}
