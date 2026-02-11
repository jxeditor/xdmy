package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.MaterialStock;

public interface IMaterialStockService {
    JSONObject findAllMaterialStock(int pageNum, int pageSize, String materialName, boolean hideZeroStock);

    int addMaterialStock(MaterialStock materialStock);

    int deleteMaterialStockById(int id);

    int updateMaterialStock(MaterialStock materialStock);

    JSONObject findMaterialNamesByPrefix(String prefix, int pageNum, int pageSize);

    int operateMaterialStock(String materialName, int quantity, boolean isIncrease);

    boolean checkMaterialExist(String materialName);

    JSONObject findMaterialOperations(int pageNum, int pageSize, String materialName, String operationType, String startDate, String endDate);

    int batchDeleteMaterialStock(String ids);

}
