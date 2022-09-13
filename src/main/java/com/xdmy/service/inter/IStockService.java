package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Stock;

public interface IStockService {
    JSONObject findAllStock(int pageNum, int pageSize, String productName);

    JSONObject findSurplusStock();

    int addStock(Stock Stock);

    int deleteStockById(int id);

    int updateStock(Stock Stock);
}
