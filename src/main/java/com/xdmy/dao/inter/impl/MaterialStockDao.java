package com.xdmy.dao.inter.impl;


import com.xdmy.dao.inter.IMaterialStockDao;
import com.xdmy.domain.MaterialStock;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MaterialStockDao extends BaseDao implements IMaterialStockDao {

    @Override
    public List<MaterialStock> findAllMaterialStock(int pageNum, int pageSize, String materialName, boolean hideZeroStock) {
        int currOffset = (pageNum - 1) * pageSize;
        String sql = "SELECT id, material_name, unitstock " +
                "FROM material_stock " +
                "WHERE 1=1";
        sql = genFilterSql(sql, materialName);
        if (hideZeroStock) {
            sql += " AND unitstock != 0";
        }
        sql += " ORDER BY unitstock DESC LIMIT ? ,?";
        return jdbcTemplate.query(sql, new RowMapper<MaterialStock>() {
            @Override
            public MaterialStock mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                MaterialStock materialStock = new MaterialStock();
                materialStock.setId(rs.getInt("id"));
                materialStock.setMaterialName(rs.getString("material_name"));
                materialStock.setUnitstock(rs.getInt("unitstock"));
                return materialStock;
            }
        }, currOffset, pageSize);
    }

    @Override
    public int getAllTotalSize(String materialName, boolean hideZeroStock) {
        String sql = "SELECT count(1) " +
                "FROM material_stock " +
                "WHERE 1=1";
        sql = genFilterSql(sql, materialName);
        if (hideZeroStock) {
            sql += " AND unitstock != 0";
        }
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public int addMaterialStock(MaterialStock materialStock) {
        String sql = "INSERT INTO material_stock(material_name, unitstock) " +
                "VALUES(?,?)";
        return jdbcTemplate.update(sql, materialStock.getMaterialName(), materialStock.getUnitstock());
    }

    @Override
    public int deleteMaterialStockById(int id) {
        String sql = "DELETE FROM material_stock WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int batchDeleteMaterialStock(String ids) {
        String sql = "DELETE FROM material_stock WHERE id IN (" + ids + ")";
        return jdbcTemplate.update(sql);
    }

    @Override
    public int updateMaterialStock(MaterialStock materialStock) {
        String sql = "UPDATE material_stock set material_name = ?, unitstock = ?" +
                " WHERE id = ? ";
        return jdbcTemplate.update(sql, materialStock.getMaterialName(), materialStock.getUnitstock(), materialStock.getId());
    }

    @Override
    public List<String> findMaterialNamesByPrefix(String prefix, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        // 优先匹配包含完整前缀的材料，然后匹配包含前缀中每个字符的材料
        String sql = "SELECT DISTINCT material_name FROM material_stock " +
                     "WHERE material_name LIKE ? OR material_name LIKE ? " +
                     "ORDER BY " +
                     "CASE " +
                     "    WHEN material_name LIKE ? THEN 0 " +
                     "    ELSE 1 " +
                     "END, " +
                     "material_name " +
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
    public int getMaterialNamesCount(String prefix) {
        // 计算包含完整前缀或包含前缀中每个字符的材料数量
        String sql = "SELECT COUNT(DISTINCT material_name) FROM material_stock " +
                     "WHERE material_name LIKE ? OR material_name LIKE ?";
        
        return jdbcTemplate.queryForObject(sql, Integer.class, 
            "%" + prefix + "%",  // 包含完整前缀
            "%" + prefix.replaceAll("", "%") + "%"  // 包含前缀中每个字符
        );
    }

    @Override
    public int operateMaterialStock(String materialName, int quantity, boolean isIncrease) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 检查材料是否存在
            String checkSql = "SELECT COUNT(*) FROM material_stock WHERE material_name = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, materialName);
            
            if (count == 0) {
                // 材料不存在，创建新记录
                String insertSql = "INSERT INTO material_stock(material_name, unitstock) " +
                        "VALUES(?, ?)";
                jdbcTemplate.update(insertSql, materialName, isIncrease ? quantity : -quantity);
            } else {
                // 材料存在，更新库存
                String updateSql = "UPDATE material_stock SET " +
                        "unitstock = unitstock " + (isIncrease ? "+" : "-") + " ? " +
                        "WHERE material_name = ?";
                jdbcTemplate.update(updateSql, quantity, materialName);
            }
            
            // 提交事务
            jdbcTemplate.execute("COMMIT");
            return 1;
        } catch (Exception e) {
            // 回滚事务
            jdbcTemplate.execute("ROLLBACK");
            e.printStackTrace();
            return 0;
        }
    }

    public String genFilterSql(String sql, String materialName) {
        if (!materialName.equals("")) {
            sql += " AND material_name like '%" + materialName + "%'";
        }
        return sql;
    }

    @Override
    public boolean checkMaterialExist(String materialName) {
        String sql = "SELECT COUNT(*) FROM material_stock WHERE material_name = ?";
        try {
            int count = jdbcTemplate.queryForObject(sql, Integer.class, materialName);
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<java.util.Map<String, Object>> findMaterialOperations(int pageNum, int pageSize, String materialName, String operationType, String startDate, String endDate) {
        int currOffset = (pageNum - 1) * pageSize;
        // 构建查询SQL，联合查询出货和入货的原材料操作记录
        String sql = "" +
            "SELECT * FROM (" +
            "    SELECT " +
            "        so.id," +
            "        '出货' as operationType," +
            "        s.odd as billNo," +
            "        s.product as productName," +
            "        s.amount as productQuantity," +
            "        so.material_name as materialName," +
            "        so.quantity as materialQuantity," +
            "        DATE_FORMAT(so.operation_date, '%Y-%m-%d %H:%i:%s') as operationDate " +
            "    FROM shipment_material_operation so " +
            "    JOIN shipment s ON so.shipment_id = s.id " +
            "    WHERE s.is_delete = 0 " +
            "    UNION ALL " +  
            "    SELECT " +
            "        io.id," +
            "        '入货' as operationType," +
            "        i.odd as billNo," +
            "        i.product as productName," +
            "        i.amount as productQuantity," +
            "        io.material_name as materialName," +
            "        io.quantity as materialQuantity," +
            "        DATE_FORMAT(io.operation_date, '%Y-%m-%d %H:%i:%s') as operationDate " +
            "    FROM incoming_material_operation io " +
            "    JOIN incoming i ON io.incoming_id = i.id " +
            "    WHERE i.is_delete = 0 " +
            ") as operations " +
            "WHERE 1=1 ";
        // 添加过滤条件
        if (materialName != null && !materialName.isEmpty()) {
            sql += " AND materialName LIKE '%" + materialName + "%'";
        }
        if (operationType != null && !operationType.isEmpty()) {
            sql += " AND operationType = '" + operationType + "'";
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql += " AND operationDate >= '" + startDate + "'";
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql += " AND operationDate <= '" + endDate + "'";
        }
        // 添加排序和分页
        sql += " ORDER BY operationDate DESC LIMIT ? ,?";
        return jdbcTemplate.queryForList(sql, currOffset, pageSize);
    }

    @Override
    public int getMaterialOperationsTotalSize(String materialName, String operationType, String startDate, String endDate) {
        // 构建查询SQL，联合查询出货和入货的原材料操作记录总数
        String sql = "" +
            "SELECT COUNT(*) " +
            "FROM ( " +
            "    SELECT " +
            "        so.id," +
            "        '出货' as operationType," +
            "        so.material_name," +
            "        so.operation_date " +
            "    FROM shipment_material_operation so " +
            "    JOIN shipment s ON so.shipment_id = s.id " +
            "    WHERE s.is_delete = 0 " +
            "    UNION ALL " +  
            "    SELECT " +
            "        io.id," +
            "        '入货' as operationType," +
            "        io.material_name," +
            "        io.operation_date " +
            "    FROM incoming_material_operation io " +
            "    JOIN incoming i ON io.incoming_id = i.id " +
            "    WHERE i.is_delete = 0 " +
            ") as operations " +
            "WHERE 1=1 " +
            "";
        // 添加过滤条件
        if (materialName != null && !materialName.isEmpty()) {
            sql += " AND material_name LIKE '%" + materialName + "%'";
        }
        if (operationType != null && !operationType.isEmpty()) {
            sql += " AND operationType = '" + operationType + "'";
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql += " AND operation_date >= '" + startDate + "'";
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql += " AND operation_date <= '" + endDate + "'";
        }
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

}
