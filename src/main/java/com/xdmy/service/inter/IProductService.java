package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Product;

/**
 * @Author xz
 * @Date 2026/2/10 12:00
 * @Description 产品Service接口
 */
public interface IProductService {
    JSONObject findAllProduct(int pageNum, int pageSize, String productName);
    
    int addProduct(Product product);
    
    int updateProduct(Product product);
    
    int deleteProductById(int id);
    
    Product findProductById(int id);
    
    JSONObject findProductNamesByPrefix(String prefix, int pageNum, int pageSize);
    
    int batchDeleteProduct(String ids);
    
    boolean checkProductExist(String productName);
}
