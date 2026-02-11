package com.xdmy.service.inter.impl;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.dao.DaoFacade;
import com.xdmy.domain.Product;
import com.xdmy.datasource.DBContextHolder;
import com.xdmy.service.inter.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author xz
 * @Date 2026/2/10 12:00
 * @Description 产品Service实现类
 */
@Service
public class ProductService implements IProductService {

    @Autowired
    private DaoFacade daoFacade;

    @Override
    public JSONObject findAllProduct(int pageNum, int pageSize, String productName) {
        DBContextHolder.setDbType("primary");
        List<Product> productList = daoFacade.getProductDao().findAllProduct(pageNum, pageSize, productName);
        int total = daoFacade.getProductDao().getAllTotalSize(productName);
        JSONObject result = toJSONObject(productList);
        result.put("total", total);
        return result;
    }

    @Override
    public int addProduct(Product product) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getProductDao().addProduct(product);
    }

    @Override
    public int updateProduct(Product product) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getProductDao().updateProduct(product);
    }

    @Override
    public int deleteProductById(int id) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getProductDao().deleteProductById(id);
    }

    @Override
    public Product findProductById(int id) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getProductDao().findProductById(id);
    }

    @Override
    public int batchDeleteProduct(String ids) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getProductDao().batchDeleteProduct(ids);
    }

    @Override
    public boolean checkProductExist(String productName) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getProductDao().checkProductExist(productName);
    }

    @Override
    public JSONObject findProductNamesByPrefix(String prefix, int pageNum, int pageSize) {
        DBContextHolder.setDbType("primary");
        List<String> productNames = daoFacade.getProductDao().findProductNamesByPrefix(prefix, pageNum, pageSize);
        int total = daoFacade.getProductDao().getProductNamesCount(prefix);
        JSONObject result = new JSONObject();
        result.put("data", productNames);
        result.put("total", total);
        return result;
    }

    private JSONObject toJSONObject(List<Product> productList) {
        JSONObject result = new JSONObject();
        result.put("data", productList);
        return result;
    }
}
