package com.xdmy.dao.inter;

import com.xdmy.domain.MaterialStock;

import java.util.List;

public interface IMaterialStockDao {
    List<MaterialStock> findAllMaterialStock(int pageNum, int pageSize, String materialName, boolean hideZeroStock, String companyName);

    int getAllTotalSize(String materialName, boolean hideZeroStock, String companyName);

    int addMaterialStock(MaterialStock materialStock, String companyName);

    int deleteMaterialStockById(int id);

    int updateMaterialStock(MaterialStock materialStock, String companyName);

    List<String> findMaterialNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);

    int getMaterialNamesCount(String prefix, String companyName);

    int operateMaterialStock(String materialName, int quantity, boolean isIncrease, String companyName);

    boolean checkMaterialExist(String materialName, String companyName);

    List<java.util.Map<String, Object>> findMaterialOperations(int pageNum, int pageSize, String materialName, String operationType, String startDate, String endDate, String companyName);

    int getMaterialOperationsTotalSize(String materialName, String operationType, String startDate, String endDate, String companyName);

    int batchDeleteMaterialStock(String ids);

}
