package com.xdmy.service.inter;

import com.xdmy.domain.Stock;
import org.json.JSONObject;

public interface IStockService {
    JSONObject findAllStock(String productName);

    int addStock(Stock Stock);

    int deleteStockById(int id);

    int updateStock(Stock Stock);
}
