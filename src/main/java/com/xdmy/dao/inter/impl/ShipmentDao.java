package com.xdmy.dao.inter.impl;


import com.xdmy.dao.inter.IShipmentDao;
import com.xdmy.domain.Shipment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.List;

//
@Repository
public class ShipmentDao extends BaseDao implements IShipmentDao {

    @Override
    public List<Shipment> findAllShipment(int pageNum, int pageSize, String customerName, String productName, String bizStartDate, String bizEndDate, String companyName) {
        int currOffset = (pageNum - 1) * pageSize;
        String sql = "SELECT * FROM shipment WHERE 1=1 AND is_delete = 0 AND company_name = ?";
        sql = genFilterSql(sql, customerName, productName, bizStartDate, bizEndDate);
        sql += " ORDER BY billdate DESC,odd DESC,id DESC LIMIT ? ,?";
        return jdbcTemplate.query(sql, new ShipmentRowMapper(), companyName, currOffset, pageSize);
    }

    @Override
    public List<Shipment> getShipmentStatement(String customerName, String bizStartDate, String bizEndDate, String companyName) {
        String sql = "SELECT * FROM shipment WHERE 1=1 AND is_delete = 0 AND paystatus = 0 AND company_name = ?" +
                " AND customer = ? AND billdate >= ? AND billdate <= ?";
        sql += " ORDER BY billdate,odd,id";
        return jdbcTemplate.query(sql, new ShipmentRowMapper(), companyName, customerName, bizStartDate, bizEndDate);
    }

    @Override
    public int getAllTotalSize(String customerName, String productName, String bizStartDate, String bizEndDate, String companyName) {
        String sql = "SELECT count(1) FROM shipment WHERE 1=1 AND is_delete = 0 AND company_name = ?";
        sql = genFilterSql(sql, customerName, productName, bizStartDate, bizEndDate);
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, companyName);
        return count != null ? count : 0;
    }

    @Override
    public int getDistinctSize(String customerName, String bizStartDate, String bizEndDate, String companyName) {
        String sql = "SELECT count(distinct odd) FROM shipment WHERE 1=1 AND is_delete = 0 AND paystatus = 0 AND company_name = ?" +
                " AND customer = ? AND billdate >= ? AND billdate <= ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, companyName, customerName, bizStartDate, bizEndDate);
        return count != null ? count : 0;
    }

    @Override
    public double getSumPay(String customerName, String bizStartDate, String bizEndDate, String companyName) {
        String sql = "SELECT sum(money) FROM shipment WHERE 1=1 AND is_delete = 0 AND paystatus = 0 AND company_name = ?" +
                " AND customer = ? AND billdate >= ? AND billdate <= ?";
        Double sum = jdbcTemplate.queryForObject(sql, Double.class, companyName, customerName, bizStartDate, bizEndDate);
        return sum != null ? sum : 0.0;
    }

    @Override
    public int addShipment(Shipment shipment, String companyName) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 添加出货记录
            String sql = "INSERT INTO shipment(odd,customer,product,billdate,amount,unitprice,money,paystatus,boardcost,fireproofboardcost,costmoney,profit,remark,is_delete,operate_material,company_name) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            // 使用KeyHolder获取新插入记录的ID
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public @NonNull PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                    java.sql.PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, shipment.getOdd());
                    ps.setString(2, shipment.getCustomer());
                    ps.setString(3, shipment.getProduct());
                    ps.setString(4, shipment.getBilldate());
                    ps.setInt(5, shipment.getAmount());
                    ps.setDouble(6, shipment.getUnitprice());
                    ps.setDouble(7, shipment.getMoney());
                    ps.setString(8, shipment.getPaystatus());
                    ps.setDouble(9, shipment.getBoardcost());
                    ps.setDouble(10, shipment.getFireproofboardcost());
                    ps.setDouble(11, shipment.getCostmoney());
                    ps.setDouble(12, shipment.getProfit());
                    ps.setString(13, shipment.getRemark());
                    ps.setString(14, "0");
                    ps.setInt(15, shipment.getOperate_material());
                    ps.setString(16, shipment.getCompany_name() != null ? shipment.getCompany_name() : "");
                    return ps;
                }
            }, keyHolder);
            // 获取新插入记录的ID
            Integer id = keyHolder.getKeyAs(Integer.class);
            if (id == null) {
                jdbcTemplate.execute("ROLLBACK");
                return 0;
            }
            int result = id;
            
            // 检查product表中是否存在对应产品的记录
            String checkProductSql = "SELECT COUNT(*) FROM product WHERE product_name = ? AND company_name = ?";
            Integer productCount = jdbcTemplate.queryForObject(checkProductSql, Integer.class, shipment.getProduct(), companyName);
            productCount = productCount != null ? productCount : 0;
            
            if (productCount == 0) {
                // 如果不存在，插入一条新记录到product表
                String insertProductSql = "INSERT INTO product(product_name, suggested_price, cost_price, maintain_material, company_name) " +
                        "VALUES(?, ?, ?, 0, ?)";
                jdbcTemplate.update(insertProductSql, shipment.getProduct(), shipment.getUnitprice(), shipment.getUnitprice(), companyName);
            }
            
            // 检查stock表中是否存在对应产品的记录
            String checkSql = "SELECT COUNT(*) FROM stock WHERE product = ? AND company_name = ?";
            Integer countObj = jdbcTemplate.queryForObject(checkSql, Integer.class, shipment.getProduct(), companyName);
            int count = countObj != null ? countObj : 0;
            
            if (count == 0) {
                // 如果不存在，插入一条新记录
                String insertSql = "INSERT INTO stock(product, unitstock, unitprice, inamount, outamount, lastoutdate, stockstatus, company_name) " +
                        "VALUES(?, 0, ?, 0, ?, ?, '1', ?)";
                jdbcTemplate.update(insertSql, shipment.getProduct(), shipment.getUnitprice(), shipment.getAmount(), shipment.getBilldate(), companyName);
            } else {
                // 如果存在，更新outamount和lastoutdate字段
                String updateSql = "UPDATE stock SET outamount = outamount + ?, lastoutdate = ? WHERE product = ? AND company_name = ?";
                jdbcTemplate.update(updateSql, shipment.getAmount(), shipment.getBilldate(), shipment.getProduct(), companyName);
            }
            
            // 操作原材料
            if (shipment.getOperate_material() == 1) {
                // 查询产品与原材料的关系
                String relationSql = "SELECT material_name, quantity FROM product_material_relation WHERE product_name = ? AND is_default = 1 AND company_name = ?";
                List<java.util.Map<String, Object>> relations = jdbcTemplate.queryForList(relationSql, shipment.getProduct(), companyName);
                
                for (java.util.Map<String, Object> relation : relations) {
                    String materialName = (String) relation.get("material_name");
                    int quantity = (int) relation.get("quantity");
                    int totalQuantity = quantity * shipment.getAmount();
                    
                    // 更新原材料库存（出货增加原材料库存）
                    String materialUpdateSql = "UPDATE material_stock SET unitstock = unitstock + ? WHERE material_name = ? AND company_name = ?";
                    int materialResult = jdbcTemplate.update(materialUpdateSql, totalQuantity, materialName, companyName);
                    
                    if (materialResult == 0) {
                        // 原材料不存在，创建新记录
                        String materialInsertSql = "INSERT INTO material_stock(material_name, unitstock, company_name) VALUES(?, ?, ?)";
                        jdbcTemplate.update(materialInsertSql, materialName, totalQuantity, companyName);
                    }
                    
                    // 记录原材料操作
                    String operationSql = "INSERT INTO shipment_material_operation(shipment_id, material_name, quantity, operation_date, company_name) VALUES(?, ?, ?, ?, ?)";
                    jdbcTemplate.update(operationSql, result, materialName, totalQuantity, new java.util.Date(), companyName);
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
    public int deleteShipmentById(int id, String companyName) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 先查询要删除的出货记录的产品和数量
            String querySql = "SELECT * FROM shipment WHERE id = ? AND company_name = ?";
            Shipment shipment = jdbcTemplate.queryForObject(querySql, new ShipmentRowMapper(), id, companyName);
            
            // 删除出货记录
            String sql = "UPDATE shipment set is_delete = 1 WHERE id = ? AND company_name = ?";
            int result = jdbcTemplate.update(sql, id, companyName);
            
            // 更新stock表中对应产品的outamount字段
            if (shipment != null) {
                String updateSql = "UPDATE stock SET outamount = outamount - ?, lastoutdate = (SELECT MAX(billdate) FROM shipment WHERE product = ? AND is_delete = 0 AND company_name = ?) WHERE product = ?";
                jdbcTemplate.update(updateSql, shipment.getAmount(), shipment.getProduct(), companyName, shipment.getProduct());
            }
            
            // 操作原材料
            // 查询原有的原材料操作记录
            String operationSql = "SELECT material_name, quantity FROM shipment_material_operation WHERE shipment_id = ?";
            List<java.util.Map<String, Object>> operations = jdbcTemplate.queryForList(operationSql, id);
            
            for (java.util.Map<String, Object> operation : operations) {
                String materialName = (String) operation.get("material_name");
                int quantity = (int) operation.get("quantity");
                
                // 更新原材料库存（删除出货记录，减少原材料库存）
                String materialUpdateSql = "UPDATE material_stock SET unitstock = unitstock - ? WHERE material_name = ? AND company_name = ?";
                jdbcTemplate.update(materialUpdateSql, quantity, materialName, companyName);
            }
            
            // 删除原材料操作记录
            String deleteOperationSql = "DELETE FROM shipment_material_operation WHERE shipment_id = ?";
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
    
    @SuppressWarnings("null")
    public int updateShipment(Shipment shipment, String materialRelationsStr, String companyName) {
        // 开始事务
        jdbcTemplate.execute("START TRANSACTION");
        try {
            // 先查询原出货记录的产品和数量
            String querySql = "SELECT * FROM shipment WHERE id = ?";
            Shipment oldShipment = jdbcTemplate.queryForObject(querySql, new ShipmentRowMapper(), shipment.getId());
            
            // 更新出货记录
            String sql = "UPDATE shipment set odd = ?,customer = ?,product = ?,billdate = ?,amount = ?,unitprice = ?,money = ?" +
                    ",paystatus = ?,boardcost = ?,fireproofboardcost = ?,costmoney = ?,profit = ?,remark = ?,operate_material = ? " +
                    "WHERE id = ? ";
            int result = jdbcTemplate.update(sql, shipment.getOdd(), shipment.getCustomer(), shipment.getProduct(), shipment.getBilldate(), shipment.getAmount(), shipment.getUnitprice(), shipment.getMoney(), shipment.getPaystatus(), shipment.getBoardcost(), shipment.getFireproofboardcost(), shipment.getCostmoney(), shipment.getProfit(), shipment.getRemark(), shipment.getOperate_material(), shipment.getId());
            
            // 如果产品没有变化
            if (oldShipment != null && shipment != null && oldShipment.getProduct().equals(shipment.getProduct())) {
                // 更新stock表中对应产品的outamount字段
                int amountDiff = shipment.getAmount() - oldShipment.getAmount();
                String updateSql = "UPDATE stock SET outamount = outamount + ?, lastoutdate = ? WHERE product = ? AND company_name = ?";
                jdbcTemplate.update(updateSql, amountDiff, shipment.getBilldate(), shipment.getProduct(), companyName);
            } else {
                // 如果产品变化了，需要更新两个产品的outamount字段和unitstock字段
                // 减少原产品的outamount
                String updateOldSql = "UPDATE stock SET outamount = outamount - ?, lastoutdate = (SELECT MAX(billdate) FROM shipment WHERE product = ? AND is_delete = 0 AND company_name = ?) WHERE product = ? AND company_name = ?";
                jdbcTemplate.update(updateOldSql, oldShipment.getAmount(), oldShipment.getProduct(), companyName, oldShipment.getProduct(), companyName);   
                
                // 检查新产品是否存在于product表中
                String checkProductSql = "SELECT COUNT(*) FROM product WHERE product_name = ? AND company_name = ?";
                Integer productCount = jdbcTemplate.queryForObject(checkProductSql, Integer.class, shipment.getProduct(), companyName);
                productCount = productCount != null ? productCount : 0;
                
                if (productCount == 0) {
                    // 如果不存在，插入一条新记录到product表
                    String insertProductSql = "INSERT INTO product(product_name, suggested_price, cost_price, maintain_material, company_name) " +
                            "VALUES(?, ?, ?, 0, ?)";
                    jdbcTemplate.update(insertProductSql, shipment.getProduct(), shipment.getUnitprice(), shipment.getUnitprice(), companyName);
                }
                
                // 增加新产品的outamount
                // 检查新产品是否在stock表中存在
                String checkSql = "SELECT COUNT(*) FROM stock WHERE product = ? AND company_name = ?";
                int count = jdbcTemplate.queryForObject(checkSql, Integer.class, shipment.getProduct(), companyName);
                
                if (count == 0) {
                    // 如果不存在，插入一条新记录
                    String insertSql = "INSERT INTO stock(product, unitstock, unitprice, inamount, outamount, lastoutdate, stockstatus, company_name) " +
                            "VALUES(?, 0, ?, 0, ?, ?, '1', ?)";
                    jdbcTemplate.update(insertSql, shipment.getProduct(), shipment.getUnitprice(), shipment.getAmount(), shipment.getBilldate(), companyName);
                } else {
                    // 如果存在，更新outamount和lastoutdate字段
                    String updateNewSql = "UPDATE stock SET outamount = outamount + ?, lastoutdate = ? WHERE product = ? AND company_name = ?";
                    jdbcTemplate.update(updateNewSql, shipment.getAmount(), shipment.getBilldate(), shipment.getProduct(), companyName);
                }
            }
            
            // 操作原材料
            // 1. 查询原有的原材料操作记录
            String oldOperationSql = "SELECT material_name, quantity FROM shipment_material_operation WHERE shipment_id = ?";
            List<java.util.Map<String, Object>> oldOperations = jdbcTemplate.queryForList(oldOperationSql, shipment.getId());
            
            // 2. 删除原有的原材料操作记录
            String deleteOperationSql = "DELETE FROM shipment_material_operation WHERE shipment_id = ?";
            jdbcTemplate.update(deleteOperationSql, shipment.getId());
            
            // 3. 如果需要操作原材料
            if (shipment.getOperate_material() == 1) {
                List<java.util.Map<String, Object>> relations = new java.util.ArrayList<>();
                
                // 如果有前端传递的原材料关系数据，使用它
                if (materialRelationsStr != null && !materialRelationsStr.isEmpty()) {
                    try {
                        // 解析JSON字符串
                        com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(materialRelationsStr);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
                            java.util.Map<String, Object> relation = new java.util.HashMap<>();
                            relation.put("material_name", jsonObject.getString("material_name"));
                            relation.put("quantity", jsonObject.getInteger("quantity"));
                            relations.add(relation);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // 解析失败，从数据库查询
                        String relationSql = "SELECT material_name, quantity FROM product_material_relation WHERE product_name = ? AND is_default = 1 AND company_name = ?";
                        relations = jdbcTemplate.queryForList(relationSql, shipment.getProduct(), companyName); 
                    }
                } else {
                    // 从数据库查询产品与原材料的关系
                    String relationSql = "SELECT material_name, quantity FROM product_material_relation WHERE product_name = ? AND is_default = 1 AND company_name = ?";
                    relations = jdbcTemplate.queryForList(relationSql, shipment.getProduct(), companyName);
                }
                
                for (java.util.Map<String, Object> relation : relations) {
                    String materialName = (String) relation.get("material_name");
                    int quantity = (int) relation.get("quantity");
                    int totalQuantity = quantity * shipment.getAmount();
                    
                    // 检查是否需要调整原材料库存
                    boolean foundOldOperation = false;
                    for (java.util.Map<String, Object> oldOperation : oldOperations) {
                        if (oldOperation.get("material_name").equals(materialName)) {
                            // 原有记录存在，计算差值
                            int oldQuantity = (int) oldOperation.get("quantity");
                            int quantityDiff = totalQuantity - oldQuantity;
                            
                            if (quantityDiff != 0) {
                                // 更新原材料库存
                                String materialUpdateSql = "UPDATE material_stock SET unitstock = unitstock + ? WHERE material_name = ? AND company_name = ?";
                                jdbcTemplate.update(materialUpdateSql, quantityDiff, materialName, companyName);
                            }
                            foundOldOperation = true;
                            break;
                        }
                    }
                    
                    if (!foundOldOperation) {
                        // 原有记录不存在，直接添加
                        // 更新原材料库存（出货增加原材料库存）
                        String materialUpdateSql = "UPDATE material_stock SET unitstock = unitstock + ? WHERE material_name = ? AND company_name = ?";
                        int materialResult = jdbcTemplate.update(materialUpdateSql, totalQuantity, materialName, companyName);
                        
                        if (materialResult == 0) {
                            // 原材料不存在，创建新记录
                            String materialInsertSql = "INSERT INTO material_stock(material_name, unitstock, company_name) VALUES(?, ?, ?)";
                            jdbcTemplate.update(materialInsertSql, materialName, totalQuantity, companyName);
                        }
                    }
                    
                    // 记录原材料操作
                    String operationSql = "INSERT INTO shipment_material_operation(shipment_id, material_name, quantity, operation_date, company_name) VALUES(?, ?, ?, ?, ?)";
                    jdbcTemplate.update(operationSql, shipment.getId(), materialName, totalQuantity, new java.util.Date(), companyName);
                }
            } else {
                // 如果不需要操作原材料，但原有记录存在，需要恢复库存
                for (java.util.Map<String, Object> oldOperation : oldOperations) {
                    String materialName = (String) oldOperation.get("material_name");
                    int oldQuantity = (int) oldOperation.get("quantity");
                    
                    // 更新原材料库存（恢复原有库存）
                    String materialUpdateSql = "UPDATE material_stock SET unitstock = unitstock - ? WHERE material_name = ? AND company_name = ?";
                    jdbcTemplate.update(materialUpdateSql, oldQuantity, materialName, companyName);
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
    public int updatePaystatusShipmentById(int id) {
        String sql = "UPDATE shipment set paystatus = '1' WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public List<String> findCustomerNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName) {
        int offset = (pageNum - 1) * pageSize;
        String sql = "SELECT DISTINCT customer FROM shipment WHERE customer LIKE ? AND is_delete = 0 AND company_name = ? ORDER BY customer LIMIT ? OFFSET ?";
        return jdbcTemplate.queryForList(sql, String.class, "%" + prefix + "%", companyName, pageSize, offset);
    }

    @Override
    public int getCustomerNamesCount(String prefix, String companyName) {
        String sql = "SELECT COUNT(DISTINCT customer) FROM shipment WHERE customer LIKE ? AND is_delete = 0 AND company_name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, "%" + prefix + "%", companyName);
    }

    @Override
    public List<java.util.Map<String, Object>> findMaterialOperationsByShipmentId(int shipmentId) {
        // 先查询出货记录是否属于该公司
        String checkSql = "SELECT COUNT(*) FROM shipment WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, shipmentId);
        if (count == null || count == 0) {
            return new java.util.ArrayList<>();
        }
        
        String sql = "SELECT * FROM shipment_material_operation WHERE shipment_id = ?";
        return jdbcTemplate.queryForList(sql, shipmentId);
    }

    @Override
    public Shipment findShipmentById(int id) {
        String sql = "SELECT * FROM shipment WHERE id = ? AND is_delete = 0";
        try {
            return jdbcTemplate.queryForObject(sql, new ShipmentRowMapper(), id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static class ShipmentRowMapper implements RowMapper<Shipment> {
        @Override
        public Shipment mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Shipment shipment = new Shipment();
            shipment.setId(rs.getInt("id"));
            shipment.setOdd(rs.getString("odd"));
            shipment.setCustomer(rs.getString("customer"));
            shipment.setProduct(rs.getString("product"));
            shipment.setBilldate(rs.getString("billdate"));
            shipment.setAmount(rs.getInt("amount"));
            shipment.setUnitprice(rs.getDouble("unitprice"));
            shipment.setMoney(rs.getDouble("money"));
            shipment.setPaystatus(rs.getString("paystatus"));
            shipment.setBoardcost(rs.getDouble("boardcost"));
            shipment.setFireproofboardcost(rs.getDouble("fireproofboardcost"));
            shipment.setCostmoney(rs.getDouble("costmoney"));
            shipment.setProfit(rs.getDouble("profit"));
            shipment.setRemark(rs.getString("remark"));
            shipment.setOperate_material(rs.getInt("operate_material"));
            return shipment;
        }
    }

    public @NonNull String genFilterSql(@NonNull String sql, String customerName, String productName, String bizStartDate, String bizEndDate) {
        if (!customerName.equals("")) {
            sql += " AND customer LIKE '%" + customerName + "%'";
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
