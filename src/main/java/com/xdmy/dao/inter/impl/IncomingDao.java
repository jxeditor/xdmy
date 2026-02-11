package com.xdmy.dao.inter.impl;


import com.xdmy.dao.inter.IIncomingDao;
import com.xdmy.domain.Incoming;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

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
        return jdbcTemplate.query(sql, new IncomingRowMapper(), currOffset, pageSize);
    }

    @Override
    public List<Incoming> getIncomingStatement(String producerName, String bizStartDate, String bizEndDate) {
        String sql = "SELECT * FROM incoming WHERE 1=1 AND is_delete = 0 AND paystatus != 2" +
                " AND producer = ? AND billdate >= ? AND billdate <= ?";
        sql += " ORDER BY billdate,odd,id";
        return jdbcTemplate.query(sql, new IncomingRowMapper(), producerName, bizStartDate, bizEndDate);

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
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public int addIncoming(Incoming incoming, String materialRelationsStr) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 添加入货记录
            String sql = "INSERT INTO incoming(odd,producer,product,billdate,amount,unitprice,money,paystatus,remark,is_delete,operate_material) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            
            // 使用GeneratedKeyHolder获取新插入记录的ID
            org.springframework.jdbc.support.GeneratedKeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                java.sql.PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, incoming.getOdd());
                ps.setString(2, incoming.getProducer());
                ps.setString(3, incoming.getProduct());
                ps.setString(4, incoming.getBilldate());
                ps.setInt(5, incoming.getAmount());
                ps.setDouble(6, incoming.getUnitprice());
                ps.setDouble(7, incoming.getMoney());
                ps.setString(8, incoming.getPaystatus());
                ps.setString(9, incoming.getRemark());
                ps.setString(10, "0");
                ps.setInt(11, incoming.getOperate_material());
                return ps;
            }, keyHolder);
            
            Number key = keyHolder.getKey();
            int result = key != null ? key.intValue() : -1;
            
            // 检查product表中是否存在对应产品的记录
            String checkProductSql = "SELECT COUNT(*) FROM product WHERE product_name = ?";
            Integer productCount = jdbcTemplate.queryForObject(checkProductSql, Integer.class, incoming.getProduct());
            productCount = productCount != null ? productCount : 0;
            
            if (productCount == 0) {
                // 如果不存在，插入一条新记录到product表
                String insertProductSql = "INSERT INTO product(product_name, suggested_price, cost_price, maintain_material) " +
                        "VALUES(?, ?, ?, 0)";
                jdbcTemplate.update(insertProductSql, incoming.getProduct(), incoming.getUnitprice(), incoming.getUnitprice());
            }
            
            // 检查stock表中是否存在对应产品的记录
            String checkSql = "SELECT COUNT(*) FROM stock WHERE product = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, incoming.getProduct());
            count = count != null ? count : 0;
            
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
            
            // 操作原材料
            if (incoming.getOperate_material() == 1) {
                java.util.List<java.util.Map<String, Object>> relations = new java.util.ArrayList<>();
                
                // 检查是否有前端传递的原材料关系数据
                if (materialRelationsStr != null && !materialRelationsStr.isEmpty()) {
                    // 解析前端传递的原材料关系数据
                    try {
                        com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSON.parseArray(materialRelationsStr);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            com.alibaba.fastjson.JSONObject jsonObj = jsonArray.getJSONObject(i);
                            java.util.Map<String, Object> relation = new java.util.HashMap<>();
                            relation.put("materialName", jsonObj.getString("materialName"));
                            relation.put("quantity", jsonObj.getInteger("quantity"));
                            relations.add(relation);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                // 如果没有前端传递的数据，从数据库查询
                if (relations.isEmpty()) {
                    String relationSql = "SELECT material_name, quantity FROM product_material_relation WHERE product_name = ? AND is_default = 1";
                    relations = jdbcTemplate.queryForList(relationSql, incoming.getProduct());
                }
                
                for (java.util.Map<String, Object> relation : relations) {
                    String materialName = (String) relation.get("materialName");
                    // 检查materialName是否为null
                    if (materialName == null || materialName.trim().isEmpty()) {
                        continue;
                    }
                    int quantity = (int) relation.get("quantity");
                    int totalQuantity = quantity * incoming.getAmount();
                    
                    // 更新原材料库存（入货减少原材料库存）
                    String materialUpdateSql = "UPDATE material_stock SET unitstock = unitstock - ? WHERE material_name = ?";
                    int materialResult = jdbcTemplate.update(materialUpdateSql, totalQuantity, materialName);
                    
                    if (materialResult == 0) {
                        // 原材料不存在，创建新记录
                        String materialInsertSql = "INSERT INTO material_stock(material_name, unitstock) VALUES(?, ?)";
                        jdbcTemplate.update(materialInsertSql, materialName, -totalQuantity);
                    }
                    
                    // 记录原材料操作
                    String operationSql = "INSERT INTO incoming_material_operation(incoming_id, material_name, quantity, operation_date) VALUES(?, ?, ?, ?)";
                    jdbcTemplate.update(operationSql, result, materialName, totalQuantity, new java.util.Date());
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
    public int addIncoming(Incoming incoming) {
        return addIncoming(incoming, null);
    }

    @Override
    public int deleteIncomingById(int id) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 先查询要删除的入货记录的产品和数量
            String querySql = "SELECT * FROM incoming WHERE id = ?";
            Incoming incoming = jdbcTemplate.queryForObject(querySql, new IncomingRowMapper(), id);
            
            // 检查incoming是否为null
            if (incoming == null) {
                jdbcTemplate.execute("ROLLBACK");
                return 0;
            }
            
            // 删除入货记录
            String sql = "UPDATE incoming set is_delete = 1 WHERE id = ?";
            int result = jdbcTemplate.update(sql, id);
            
            // 更新stock表中对应产品的inamount和purchaseprice字段
            String updateSql = "UPDATE stock SET inamount = inamount - ?, " +
                    "purchaseprice = (SELECT unitprice FROM incoming WHERE product = ? AND is_delete = 0 ORDER BY billdate DESC LIMIT 1), " +
                    "lastindate = (SELECT MAX(billdate) FROM incoming WHERE product = ? AND is_delete = 0) " +
                    "WHERE product = ?";
            jdbcTemplate.update(updateSql, incoming.getAmount(), incoming.getProduct(), incoming.getProduct(), incoming.getProduct());
            
            // 操作原材料
            // 查询原有的原材料操作记录
            String operationSql = "SELECT material_name, quantity FROM incoming_material_operation WHERE incoming_id = ?";
            List<java.util.Map<String, Object>> operations = jdbcTemplate.queryForList(operationSql, id);
            
            for (java.util.Map<String, Object> operation : operations) {
                String materialName = (String) operation.get("material_name");
                int quantity = (int) operation.get("quantity");
                
                // 更新原材料库存（删除入货记录，增加原材料库存）
                String materialUpdateSql = "UPDATE material_stock SET unitstock = unitstock + ? WHERE material_name = ?";
                jdbcTemplate.update(materialUpdateSql, quantity, materialName);
            }
            
            // 删除原材料操作记录
            String deleteOperationSql = "DELETE FROM incoming_material_operation WHERE incoming_id = ?";
            jdbcTemplate.update(deleteOperationSql, id);
            
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
    public int updateIncoming(Incoming incoming, String materialRelationsStr) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 先查询原入货记录的产品和数量
            String querySql = "SELECT * FROM incoming WHERE id = ?";
            Incoming oldIncoming = jdbcTemplate.queryForObject(querySql, new IncomingRowMapper(), incoming.getId());
            
            // 检查oldIncoming是否为null
            if (oldIncoming == null) {
                jdbcTemplate.execute("ROLLBACK");
                return 0;
            }
            
            // 更新入货记录
            String sql = "UPDATE incoming set odd = ?,producer = ?,product = ?,billdate = ?,amount = ?,unitprice = ?,money = ? " +
                    ",paystatus = ?,remark = ?,operate_material = ? " +
                    "WHERE id = ? ";
            int result = jdbcTemplate.update(sql, incoming.getOdd(), incoming.getProducer(), incoming.getProduct(), incoming.getBilldate(), incoming.getAmount(), incoming.getUnitprice(), incoming.getMoney(), incoming.getPaystatus(), incoming.getRemark(), incoming.getOperate_material(), incoming.getId());
            
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
                
                // 检查新产品是否存在于product表中
                String checkProductSql = "SELECT COUNT(*) FROM product WHERE product_name = ?";
                Integer productCount = jdbcTemplate.queryForObject(checkProductSql, Integer.class, incoming.getProduct());
                productCount = productCount != null ? productCount : 0;
                
                if (productCount == 0) {
                    // 如果不存在，插入一条新记录到product表
                    String insertProductSql = "INSERT INTO product(product_name, suggested_price, cost_price, maintain_material) " +
                            "VALUES(?, ?, ?, 0)";
                    jdbcTemplate.update(insertProductSql, incoming.getProduct(), incoming.getUnitprice(), incoming.getUnitprice());
                }
                
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
            
            // 操作原材料
            // 1. 如果需要操作原材料
            if (incoming.getOperate_material() == 1) {
                java.util.List<java.util.Map<String, Object>> relations = new java.util.ArrayList<>();
                
                // 检查是否有前端传递的原材料关系数据
                if (materialRelationsStr != null && !materialRelationsStr.isEmpty()) {
                    // 解析前端传递的原材料关系数据
                    try {
                        com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSON.parseArray(materialRelationsStr);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            com.alibaba.fastjson.JSONObject jsonObj = jsonArray.getJSONObject(i);
                            java.util.Map<String, Object> relation = new java.util.HashMap<>();
                            relation.put("materialName", jsonObj.getString("materialName"));
                            relation.put("quantity", jsonObj.getInteger("quantity"));
                            relations.add(relation);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                // 如果没有前端传递的数据，从数据库查询
                if (relations.isEmpty()) {
                    String relationSql = "SELECT material_name, quantity FROM product_material_relation WHERE product_name = ? AND is_default = 1";
                    relations = jdbcTemplate.queryForList(relationSql, new Object[]{incoming.getProduct()});
                }
                
                // 计算原有原材料操作记录的总量，用于恢复库存
                java.util.Map<String, Integer> oldMaterialQuantities = new java.util.HashMap<>();
                String oldOperationSql = "SELECT material_name, quantity FROM incoming_material_operation WHERE incoming_id = ?";
                java.util.List<java.util.Map<String, Object>> oldOperations = jdbcTemplate.queryForList(oldOperationSql, incoming.getId());
                
                // 删除原有的原材料操作记录
                String deleteOperationSql = "DELETE FROM incoming_material_operation WHERE incoming_id = ?";
                jdbcTemplate.update(deleteOperationSql, incoming.getId());
                for (java.util.Map<String, Object> oldOp : oldOperations) {
                    oldMaterialQuantities.put((String) oldOp.get("material_name"), (int) oldOp.get("quantity"));
                }
                
                for (java.util.Map<String, Object> relation : relations) {
                    String materialName = (String) relation.get("materialName");
                    // 检查materialName是否为null
                    if (materialName == null || materialName.trim().isEmpty()) {
                        continue;
                    }
                    int quantity = (int) relation.get("quantity");
                    int totalQuantity = quantity * incoming.getAmount();
                    
                    // 恢复原有库存
                    if (oldMaterialQuantities.containsKey(materialName)) {
                        int oldQuantity = oldMaterialQuantities.get(materialName);
                        // 恢复库存（增加库存，因为原来的入货减少了库存）
                        String restoreSql = "UPDATE material_stock SET unitstock = unitstock + ? WHERE material_name = ?";
                        jdbcTemplate.update(restoreSql, oldQuantity, materialName);
                    }
                    
                    // 更新原材料库存（入货减少原材料库存）
                    String materialUpdateSql = "UPDATE material_stock SET unitstock = unitstock - ? WHERE material_name = ?";
                    int materialResult = jdbcTemplate.update(materialUpdateSql, totalQuantity, materialName);
                    
                    if (materialResult == 0) {
                        // 原材料不存在，创建新记录
                        String materialInsertSql = "INSERT INTO material_stock(material_name, unitstock) VALUES(?, ?)";
                        jdbcTemplate.update(materialInsertSql, materialName, -totalQuantity);
                    }
                    
                    // 记录原材料操作
                    String operationSql = "INSERT INTO incoming_material_operation(incoming_id, material_name, quantity, operation_date) VALUES(?, ?, ?, ?)";
                    jdbcTemplate.update(operationSql, incoming.getId(), materialName, totalQuantity, new java.util.Date());
                }
                
                // 处理不再使用的原材料，恢复它们的库存
                for (java.util.Map.Entry<String, Integer> entry : oldMaterialQuantities.entrySet()) {
                    String materialName = entry.getKey();
                    // 检查material_name是否为null
                    if (materialName == null || materialName.trim().isEmpty()) {
                        continue;
                    }
                    boolean stillUsed = false;
                    for (java.util.Map<String, Object> relation : relations) {
                        String relMaterialName = (String) relation.get("material_name");
                        if (materialName.equals(relMaterialName)) {
                            stillUsed = true;
                            break;
                        }
                    }
                    if (!stillUsed) {
                        int oldQuantity = entry.getValue();
                        // 恢复库存（增加库存，因为原来的入货减少了库存）
                        String restoreSql = "UPDATE material_stock SET unitstock = unitstock + ? WHERE material_name = ?";
                        jdbcTemplate.update(restoreSql, oldQuantity, materialName);
                    }
                }
            } else {
                // 如果不再操作原材料，需要恢复所有原有原材料的库存
                String oldOperationSql = "SELECT material_name, quantity FROM incoming_material_operation WHERE incoming_id = ?";
                java.util.List<java.util.Map<String, Object>> oldOperations = jdbcTemplate.queryForList(oldOperationSql, new Object[]{incoming.getId()});
                for (java.util.Map<String, Object> oldOp : oldOperations) {
                    String materialName = (String) oldOp.get("material_name");
                    // 检查material_name是否为null
                    if (materialName == null || materialName.trim().isEmpty()) {
                        continue;
                    }
                    int oldQuantity = (int) oldOp.get("quantity");
                    // 恢复库存（增加库存，因为原来的入货减少了库存）
                    String restoreSql = "UPDATE material_stock SET unitstock = unitstock + ? WHERE material_name = ?";
                    jdbcTemplate.update(restoreSql, oldQuantity, materialName);
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
    public int updateIncoming(Incoming incoming) {
        return updateIncoming(incoming, null);
    }

    @Override
    public int updatePaystatusIncomingById(int id) {
        String sql = "UPDATE incoming set paystatus = '2' WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    static class IncomingRowMapper implements RowMapper<Incoming> {
        @Override
        public Incoming mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
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
            incoming.setOperate_material(rs.getInt("operate_material"));
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
        
        return jdbcTemplate.queryForList(sql, String.class, 
            "%" + prefix + "%",  // 包含完整前缀
            "%" + prefix.replaceAll("", "%") + "%",  // 包含前缀中每个字符
            "%" + prefix + "%",  // 用于排序
            pageSize, 
            offset
        );
    }

    @Override
    public int getProducerNamesCount(String prefix) {
        // 计算包含完整前缀或包含前缀中每个字符的供应商数量
        String sql = "SELECT COUNT(DISTINCT producer) FROM incoming " +
                     "WHERE is_delete = 0 AND (producer LIKE ? OR producer LIKE ?)";
        
        return jdbcTemplate.queryForObject(sql, Integer.class, 
            "%" + prefix + "%",  // 包含完整前缀
            "%" + prefix.replaceAll("", "%") + "%"  // 包含前缀中每个字符
        );
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> getIncomingMaterialOperations(int id) {
        String sql = "SELECT material_name, quantity FROM incoming_material_operation WHERE incoming_id = ?";
        return jdbcTemplate.queryForList(sql, id);
    }

    @Override
    public Incoming findIncomingById(int id) {
        String sql = "SELECT * FROM incoming WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new IncomingRowMapper(), id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
