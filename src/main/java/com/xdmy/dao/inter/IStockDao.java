package com.xdmy.dao.inter;

import com.xdmy.domain.Stock;

import java.util.List;

public interface IStockDao {
    List<Stock> findAllStock(int pageNum, int pageSize, String productName, boolean hideZeroStock);

    List<Stock> findSurplusStock();

    int getAllTotalSize(String productName, boolean hideZeroStock);

    int addStock(Stock Stock);

    int deleteStockById(int id);

    int updateStock(Stock Stock);

    List<String> findProductNamesByPrefix(String prefix, int pageNum, int pageSize);

    int getProductNamesCount(String prefix);

    int flattenStock();
    
    int getFlattenStockCount();
    
    int batchDeleteStock(String ids);

}
