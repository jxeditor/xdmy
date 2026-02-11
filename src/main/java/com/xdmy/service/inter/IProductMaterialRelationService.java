package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.ProductMaterialRelation;

/**
 * @Author xz
 * @Date 2026/2/10 11:00
 * @Description 产品与原材料关系Service接口
 */
public interface IProductMaterialRelationService {
    JSONObject findAllRelation(int pageNum, int pageSize, String productName);
    int addRelation(ProductMaterialRelation relation);
    int updateRelation(ProductMaterialRelation relation);
    int deleteRelationById(int id);
    JSONObject findRelationsByProductName(String productName);
    boolean checkRelationUnique(String productName, String materialName, Integer id);
    int batchDeleteRelation(String ids);
}
