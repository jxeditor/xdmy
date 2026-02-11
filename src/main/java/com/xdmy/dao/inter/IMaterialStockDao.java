package com.xdmy.dao.inter;

import com.xdmy.domain.MaterialStock;

import java.util.List;

public interface IMaterialStockDao {
    List<MaterialStock> findAllMaterialStock(int pageNum, int pageSize, String materialName, boolean hideZeroStock);

    int getAllTotalSize(String materialName, boolean hideZeroStock);

    int addMaterialStock(MaterialStock materialStock);

    int deleteMaterialStockById(int id);

    int updateMaterialStock(MaterialStock materialStock);

    List<String> findMaterialNamesByPrefix(String prefix, int pageNum, int pageSize);

    int getMaterialNamesCount(String prefix);

    int operateMaterialStock(String materialName, int quantity, boolean isIncrease);

    boolean checkMaterialExist(String materialName);

    List<java.util.Map<String, Object>> findMaterialOperations(int pageNum, int pageSize, String materialName, String operationType, String startDate, String endDate);

    int getMaterialOperationsTotalSize(String materialName, String operationType, String startDate, String endDate);

    int batchDeleteMaterialStock(String ids);

}
