package com.xdmy.dao.inter;

import com.xdmy.domain.Product;

import java.util.List;

/**
 * @Author xz
 * @Date 2026/2/10 12:00
 * @Description 产品DAO接口
 */
public interface IProductDao {
    List<Product> findAllProduct(int pageNum, int pageSize, String productName);
    
    int getAllTotalSize(String productName);
    
    int addProduct(Product product);
    
    int updateProduct(Product product);
    
    int deleteProductById(int id);
    
    Product findProductById(int id);
    
    List<String> findProductNamesByPrefix(String prefix, int pageNum, int pageSize);
    
    int getProductNamesCount(String prefix);
    
    int batchDeleteProduct(String ids);
    
    boolean checkProductExist(String productName);
}
