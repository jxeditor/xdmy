package com.xdmy.service.inter.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xdmy.datasource.DBContextHolder;
import com.xdmy.domain.Stock;
import com.xdmy.service.inter.IStockService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService extends BaseService implements IStockService {
    @Override
    public JSONObject findAllStock(int pageNum, int pageSize, String productName) {
        DBContextHolder.setDbType("primary");
        List<Stock> stockList = daoFacade.getStockDao().findAllStock(pageNum, pageSize, productName);
        int total = daoFacade.getStockDao().getAllTotalSize(productName);
        JSONObject result = toJSONObject(stockList);
        result.put("total", total);
        return result;
    }

    @Override
    public int addStock(Stock stock) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getStockDao().addStock(stock);
    }

    @Override
    public int deleteStockById(int id) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getStockDao().deleteStockById(id);
    }

    @Override
    public int updateStock(Stock stock) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getStockDao().updateStock(stock);
    }

    public JSONObject toJSONObject(List<Stock> stockList) {
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        try {
            if (stockList != null) {
                for (Stock stock : stockList) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", stock.getId());
                    obj.put("product", stock.getProduct());
                    obj.put("unitstock", stock.getUnitstock());
                    obj.put("unitprice", stock.getUnitprice());
                    obj.put("inamount", stock.getInamount());
                    obj.put("outamount", stock.getOutamount());
                    obj.put("stock", stock.getStock());
                    obj.put("money", stock.getMoney());
                    obj.put("lastindate", stock.getLastindate());
                    obj.put("lastoutdate", stock.getLastoutdate());
                    obj.put("stockstatus", stock.getStockstatus());
                    data.add(obj);
                }
            }
            result.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
