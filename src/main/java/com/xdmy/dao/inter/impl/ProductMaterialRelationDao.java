package com.xdmy.dao.inter.impl;

import com.xdmy.dao.inter.IProductMaterialRelationDao;
import com.xdmy.domain.ProductMaterialRelation;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author xz
 * @Date 2026/2/10 11:00
 * @Description 产品与原材料关系DAO实现类
 */
@Repository
public class ProductMaterialRelationDao extends BaseDao implements IProductMaterialRelationDao {

    @Override
    public List<ProductMaterialRelation> findAllRelation(int pageNum, int pageSize, String productName) {
        int currOffset = (pageNum - 1) * pageSize;
        String sql = "SELECT * FROM product_material_relation WHERE 1=1";
        if (productName != null && !productName.equals("")) {
            sql += " AND product_name LIKE '%" + productName + "%'";
        }
        sql += " ORDER BY id DESC LIMIT ? ,?";
        return jdbcTemplate.query(sql, new RowMapper<ProductMaterialRelation>() {
            @Override
            public ProductMaterialRelation mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                ProductMaterialRelation relation = new ProductMaterialRelation();
                relation.setId(rs.getInt("id"));
                relation.setProductName(rs.getString("product_name"));
                relation.setMaterialName(rs.getString("material_name"));
                relation.setQuantity(rs.getInt("quantity"));
                relation.setIsDefault(rs.getInt("is_default"));
                return relation;
            }
        }, currOffset, pageSize);
    }

    @Override
    public int getAllTotalSize(String productName) {
        String sql = "SELECT count(1) FROM product_material_relation WHERE 1=1";
        if (productName != null && !productName.equals("")) {
            sql += " AND product_name LIKE '%" + productName + "%'";
        }
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public int addRelation(ProductMaterialRelation relation) {
        String sql = "INSERT INTO product_material_relation(product_name, material_name, quantity, is_default) VALUES(?, ?, ?, ?)";
        return jdbcTemplate.update(sql, relation.getProductName(), relation.getMaterialName(), relation.getQuantity(), relation.getIsDefault());
    }

    @Override
    public int updateRelation(ProductMaterialRelation relation) {
        String sql = "UPDATE product_material_relation SET product_name = ?, material_name = ?, quantity = ?, is_default = ? WHERE id = ?";
        return jdbcTemplate.update(sql, relation.getProductName(), relation.getMaterialName(), relation.getQuantity(), relation.getIsDefault(), relation.getId());
    }

    @Override
    public int deleteRelationById(int id) {
        String sql = "DELETE FROM product_material_relation WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int batchDeleteRelation(String ids) {
        String sql = "DELETE FROM product_material_relation WHERE id IN (" + ids + ")";
        return jdbcTemplate.update(sql);
    }

    @Override
    public List<ProductMaterialRelation> findRelationsByProductName(String productName) {
        String sql = "SELECT * FROM product_material_relation WHERE product_name = ?";
        return jdbcTemplate.query(sql, new RowMapper<ProductMaterialRelation>() {
            @Override
            public ProductMaterialRelation mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                ProductMaterialRelation relation = new ProductMaterialRelation();
                relation.setId(rs.getInt("id"));
                relation.setProductName(rs.getString("product_name"));
                relation.setMaterialName(rs.getString("material_name"));
                relation.setQuantity(rs.getInt("quantity"));
                relation.setIsDefault(rs.getInt("is_default"));
                return relation;
            }
        }, productName);
    }

    @Override
    public boolean checkRelationUnique(String productName, String materialName, Integer id) {
        String sql;
        if (id != null) {
            // 更新操作，排除当前记录
            sql = "SELECT count(1) FROM product_material_relation WHERE product_name = ? AND material_name = ? AND id != ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, productName, materialName, id);
            return count == 0;
        } else {
            // 添加操作，检查是否存在
            sql = "SELECT count(1) FROM product_material_relation WHERE product_name = ? AND material_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, productName, materialName);
            return count == 0;
        }
    }

    @Override
    public List<String> findProductNamesByPrefix(String prefix, int pageNum, int pageSize) {
        int currOffset = (pageNum - 1) * pageSize;
        // 优先匹配包含完整前缀的产品，然后匹配包含前缀中每个字符的产品
        String sql = "SELECT DISTINCT product_name FROM product_material_relation " +
                     "WHERE product_name LIKE ? OR product_name LIKE ? " +
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
            currOffset
        );
    }

    @Override
    public int getProductNamesCountByPrefix(String prefix) {
        // 计算包含完整前缀或包含前缀中每个字符的产品数量
        String sql = "SELECT COUNT(DISTINCT product_name) FROM product_material_relation " +
                     "WHERE product_name LIKE ? OR product_name LIKE ?";
        
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, 
            "%" + prefix + "%",  // 包含完整前缀
            "%" + prefix.replaceAll("", "%") + "%"  // 包含前缀中每个字符
        );
        return count != null ? count : 0;
    }
}
