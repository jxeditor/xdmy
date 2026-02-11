package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Stock;

import java.util.List;

public interface IStockService {
    JSONObject findAllStock(int pageNum, int pageSize, String productName, boolean hideZeroStock);

    JSONObject findSurplusStock();

    int addStock(Stock Stock);

    int deleteStockById(int id);

    int updateStock(Stock Stock);

    JSONObject findProductNamesByPrefix(String prefix, int pageNum, int pageSize);

    int flattenStock();
    
    int getFlattenStockCount();
    
    int batchDeleteStock(String ids);
}
