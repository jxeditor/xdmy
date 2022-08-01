package com.xdmy.dao.inter;

import com.xdmy.domain.Stock;

import java.util.List;

public interface IStockDao {
    List<Stock> findAllStock(int pageNum, int pageSize,String productName);

    int getAllTotalSize(String productName);

    int addStock(Stock Stock);

    int deleteStockById(int id);

    int updateStock(Stock Stock);
}
