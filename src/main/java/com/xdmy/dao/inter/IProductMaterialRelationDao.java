package com.xdmy.dao.inter;

import com.xdmy.domain.ProductMaterialRelation;
import java.util.List;

/**
 * @Author xz
 * @Date 2026/2/10 11:00
 * @Description 产品与原材料关系DAO接口
 */
public interface IProductMaterialRelationDao {
    List<ProductMaterialRelation> findAllRelation(int pageNum, int pageSize, String productName, String companyName);
    int getAllTotalSize(String productName, String companyName);
    int addRelation(ProductMaterialRelation relation, String companyName);
    int updateRelation(ProductMaterialRelation relation, String companyName);
    int deleteRelationById(int id);
    List<ProductMaterialRelation> findRelationsByProductName(String productName, String companyName);
    boolean checkRelationUnique(String productName, String materialName, Integer id, String companyName);
    int batchDeleteRelation(String ids);
    List<String> findProductNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);
    int getProductNamesCountByPrefix(String prefix, String companyName);
}
