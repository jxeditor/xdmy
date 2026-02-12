package com.xdmy.dao.inter;

import com.xdmy.domain.Stock;

import java.util.List;

public interface IStockDao {
    List<Stock> findAllStock(int pageNum, int pageSize, String productName, boolean hideZeroStock, String companyName);

    List<Stock> findSurplusStock(String companyName);

    int getAllTotalSize(String productName, boolean hideZeroStock, String companyName);

    int addStock(Stock Stock);

    int deleteStockById(int id);

    int updateStock(Stock Stock);

    List<String> findProductNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);

    int getProductNamesCount(String prefix, String companyName);

    int flattenStock(String companyName);
    
    int getFlattenStockCount(String companyName);
    
    int batchDeleteStock(String ids);

}
