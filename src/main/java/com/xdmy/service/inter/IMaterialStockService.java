package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.MaterialStock;

public interface IMaterialStockService {
    JSONObject findAllMaterialStock(int pageNum, int pageSize, String materialName, boolean hideZeroStock, String companyName);

    int addMaterialStock(MaterialStock materialStock, String companyName);

    int deleteMaterialStockById(int id);

    int updateMaterialStock(MaterialStock materialStock, String companyName);

    JSONObject findMaterialNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);

    int operateMaterialStock(String materialName, int quantity, boolean isIncrease, String companyName);

    boolean checkMaterialExist(String materialName, String companyName);

    JSONObject findMaterialOperations(int pageNum, int pageSize, String materialName, String operationType, String startDate, String endDate, String companyName);

    int batchDeleteMaterialStock(String ids);

}
