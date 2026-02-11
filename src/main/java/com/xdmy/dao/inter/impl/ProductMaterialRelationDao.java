package com.xdmy.dao.inter.impl;

import com.xdmy.dao.inter.IProductMaterialRelationDao;
import com.xdmy.domain.ProductMaterialRelation;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
        return jdbcTemplate.query(sql, new Object[]{currOffset, pageSize}, new RowMapper<ProductMaterialRelation>() {
            @Override
            public ProductMaterialRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
                ProductMaterialRelation relation = new ProductMaterialRelation();
                relation.setId(rs.getInt("id"));
                relation.setProductName(rs.getString("product_name"));
                relation.setMaterialName(rs.getString("material_name"));
                relation.setQuantity(rs.getInt("quantity"));
                relation.setIsDefault(rs.getInt("is_default"));
                return relation;
            }
        });
    }

    @Override
    public int getAllTotalSize(String productName) {
        String sql = "SELECT count(1) FROM product_material_relation WHERE 1=1";
        if (productName != null && !productName.equals("")) {
            sql += " AND product_name LIKE '%" + productName + "%'";
        }
        return jdbcTemplate.queryForObject(sql, Integer.class);
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
        return jdbcTemplate.query(sql, new Object[]{productName}, new RowMapper<ProductMaterialRelation>() {
            @Override
            public ProductMaterialRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
                ProductMaterialRelation relation = new ProductMaterialRelation();
                relation.setId(rs.getInt("id"));
                relation.setProductName(rs.getString("product_name"));
                relation.setMaterialName(rs.getString("material_name"));
                relation.setQuantity(rs.getInt("quantity"));
                relation.setIsDefault(rs.getInt("is_default"));
                return relation;
            }
        });
    }

    @Override
    public boolean checkRelationUnique(String productName, String materialName, Integer id) {
        String sql;
        if (id != null) {
            // 更新操作，排除当前记录
            sql = "SELECT count(1) FROM product_material_relation WHERE product_name = ? AND material_name = ? AND id != ?";
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{productName, materialName, id}, Integer.class);
            return count == 0;
        } else {
            // 添加操作，检查是否存在
            sql = "SELECT count(1) FROM product_material_relation WHERE product_name = ? AND material_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{productName, materialName}, Integer.class);
            return count == 0;
        }
    }
}
