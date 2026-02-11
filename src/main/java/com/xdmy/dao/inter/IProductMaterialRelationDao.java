package com.xdmy.dao.inter;

import com.xdmy.domain.ProductMaterialRelation;
import java.util.List;

/**
 * @Author xz
 * @Date 2026/2/10 11:00
 * @Description 产品与原材料关系DAO接口
 */
public interface IProductMaterialRelationDao {
    List<ProductMaterialRelation> findAllRelation(int pageNum, int pageSize, String productName);
    int getAllTotalSize(String productName);
    int addRelation(ProductMaterialRelation relation);
    int updateRelation(ProductMaterialRelation relation);
    int deleteRelationById(int id);
    List<ProductMaterialRelation> findRelationsByProductName(String productName);
    boolean checkRelationUnique(String productName, String materialName, Integer id);
    int batchDeleteRelation(String ids);
}
