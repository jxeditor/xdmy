package com.xdmy.dao.inter;

import com.xdmy.domain.Product;

import java.util.List;

/**
 * @Author xz
 * @Date 2026/2/10 12:00
 * @Description 产品DAO接口
 */
public interface IProductDao {
    List<Product> findAllProduct(int pageNum, int pageSize, String productName, String companyName);
    
    int getAllTotalSize(String productName, String companyName);
    
    int addProduct(Product product, String companyName);
    
    int updateProduct(Product product, String companyName);
    
    int deleteProductById(int id);
    
    Product findProductById(int id);
    
    List<String> findProductNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);
    
    int getProductNamesCount(String prefix, String companyName);
    
    int batchDeleteProduct(String ids);
    
    boolean checkProductExist(String productName, String companyName);

    
}
