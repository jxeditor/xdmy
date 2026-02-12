package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Product;

/**
 * @Author xz
 * @Date 2026/2/10 12:00
 * @Description 产品Service接口
 */
public interface IProductService {
    JSONObject findAllProduct(int pageNum, int pageSize, String productName, String companyName);
    
    int addProduct(Product product, String companyName);
    
    int updateProduct(Product product, String companyName);
    
    int deleteProductById(int id);
    
    Product findProductById(int id);
    
    JSONObject findProductNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);
    
    int batchDeleteProduct(String ids);
    
    boolean checkProductExist(String productName, String companyName);
}
