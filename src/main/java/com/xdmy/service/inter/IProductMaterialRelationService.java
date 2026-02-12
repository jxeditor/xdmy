package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.ProductMaterialRelation;

/**
 * @Author xz
 * @Date 2026/2/10 11:00
 * @Description 产品与原材料关系Service接口
 */
public interface IProductMaterialRelationService {
    JSONObject findAllRelation(int pageNum, int pageSize, String productName, String companyName);
    int addRelation(ProductMaterialRelation relation, String companyName);
    int updateRelation(ProductMaterialRelation relation, String companyName);
    int deleteRelationById(int id);
    JSONObject findRelationsByProductName(String productName, String companyName);
    boolean checkRelationUnique(String productName, String materialName, Integer id, String companyName);
    int batchDeleteRelation(String ids);
    JSONObject findProductNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);
}
