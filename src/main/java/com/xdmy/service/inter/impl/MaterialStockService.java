package com.xdmy.service.inter.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xdmy.datasource.DBContextHolder;
import com.xdmy.domain.MaterialStock;
import com.xdmy.service.inter.IMaterialStockService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialStockService extends BaseService implements IMaterialStockService {
    @Override
    public JSONObject findAllMaterialStock(int pageNum, int pageSize, String materialName, boolean hideZeroStock) {
        DBContextHolder.setDbType("primary");
        List<MaterialStock> materialStockList = daoFacade.getMaterialStockDao().findAllMaterialStock(pageNum, pageSize, materialName, hideZeroStock);
        int total = daoFacade.getMaterialStockDao().getAllTotalSize(materialName, hideZeroStock);
        JSONObject result = toJSONObject(materialStockList);
        result.put("total", total);
        return result;
    }

    @Override
    public int addMaterialStock(MaterialStock materialStock) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getMaterialStockDao().addMaterialStock(materialStock);
    }

    @Override
    public int deleteMaterialStockById(int id) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getMaterialStockDao().deleteMaterialStockById(id);
    }

    @Override
    public int batchDeleteMaterialStock(String ids) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getMaterialStockDao().batchDeleteMaterialStock(ids);
    }

    @Override
    public int updateMaterialStock(MaterialStock materialStock) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getMaterialStockDao().updateMaterialStock(materialStock);
    }

    @Override
    public JSONObject findMaterialNamesByPrefix(String prefix, int pageNum, int pageSize) {
        DBContextHolder.setDbType("primary");
        List<String> materialNames = daoFacade.getMaterialStockDao().findMaterialNamesByPrefix(prefix, pageNum, pageSize);
        int total = daoFacade.getMaterialStockDao().getMaterialNamesCount(prefix);
        JSONObject result = new JSONObject();
        result.put("data", materialNames);
        result.put("total", total);
        return result;
    }

    @Override
    public int operateMaterialStock(String materialName, int quantity, boolean isIncrease) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getMaterialStockDao().operateMaterialStock(materialName, quantity, isIncrease);
    }

    @Override
    public boolean checkMaterialExist(String materialName) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getMaterialStockDao().checkMaterialExist(materialName);
    }

    public JSONObject toJSONObject(List<MaterialStock> materialStockList) {
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        try {
            if (materialStockList != null) {
                for (MaterialStock materialStock : materialStockList) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", materialStock.getId());
                    obj.put("materialName", materialStock.getMaterialName());
                    obj.put("unitstock", materialStock.getUnitstock());
                    data.add(obj);
                }
            }
            result.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject findMaterialOperations(int pageNum, int pageSize, String materialName, String operationType, String startDate, String endDate) {
        DBContextHolder.setDbType("primary");
        List<java.util.Map<String, Object>> operations = daoFacade.getMaterialStockDao().findMaterialOperations(pageNum, pageSize, materialName, operationType, startDate, endDate);
        int total = daoFacade.getMaterialStockDao().getMaterialOperationsTotalSize(materialName, operationType, startDate, endDate);
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        // 日期格式化器
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (operations != null) {
                for (java.util.Map<String, Object> operation : operations) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", operation.get("id"));
                    obj.put("operationType", operation.get("operationType"));
                    obj.put("billNo", operation.get("billNo"));
                    obj.put("productName", operation.get("productName"));
                    obj.put("productQuantity", operation.get("productQuantity"));
                    obj.put("materialName", operation.get("materialName"));
                    obj.put("materialQuantity", operation.get("materialQuantity"));
                    // 格式化日期
                    Object dateObj = operation.get("operationDate");
                    if (dateObj != null) {
                        String formattedDate = "";
                        if (dateObj instanceof java.util.Date) {
                            // 如果是Date类型，直接格式化
                            formattedDate = sdf.format(dateObj);
                        } else if (dateObj instanceof java.sql.Timestamp) {
                            // 如果是Timestamp类型，转换为Date后格式化
                            formattedDate = sdf.format((java.sql.Timestamp) dateObj);
                        } else if (dateObj instanceof String) {
                            // 如果是String类型，替换T为空格
                            formattedDate = ((String) dateObj).replace('T', ' ');
                        } else {
                            // 其他类型，直接转换为字符串
                            formattedDate = dateObj.toString();
                        }
                        obj.put("operationDate", formattedDate);
                    }
                    data.add(obj);
                }
            }
            result.put("data", data);
            result.put("total", total);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
