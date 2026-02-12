package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Stock;

public interface IStockService {
    
    JSONObject findAllStock(int pageNum, int pageSize, String productName, boolean hideZeroStock, String companyName);

    JSONObject findSurplusStock(String companyName);

    int addStock(Stock Stock);

    int deleteStockById(int id);

    int updateStock(Stock Stock);

    JSONObject findProductNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);

    int flattenStock(String companyName);
    
    int getFlattenStockCount(String companyName);
    
    int batchDeleteStock(String ids);
}
