package com.xdmy.service.inter.impl;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.dao.DaoFacade;
import com.xdmy.domain.ProductMaterialRelation;
import com.xdmy.service.inter.IProductMaterialRelationService;
import com.xdmy.datasource.DBContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author xz
 * @Date 2026/2/10 11:00
 * @Description 产品与原材料关系Service实现类
 */
@Service
public class ProductMaterialRelationService implements IProductMaterialRelationService {

    @Autowired
    private DaoFacade daoFacade;

    @Override
    public JSONObject findAllRelation(int pageNum, int pageSize, String productName, String companyName) {
        DBContextHolder.setDbType("primary");
        List<ProductMaterialRelation> relationList = daoFacade.getProductMaterialRelationDao().findAllRelation(pageNum, pageSize, productName, companyName);
        int total = daoFacade.getProductMaterialRelationDao().getAllTotalSize(productName, companyName);
        JSONObject result = toJSONObject(relationList);
        result.put("total", total);
        return result;
    }

    @Override
    public int addRelation(ProductMaterialRelation relation, String companyName) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getProductMaterialRelationDao().addRelation(relation, companyName);
    }

    @Override
    public int updateRelation(ProductMaterialRelation relation, String companyName) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getProductMaterialRelationDao().updateRelation(relation, companyName);
    }

    @Override
    public int deleteRelationById(int id) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getProductMaterialRelationDao().deleteRelationById(id);
    }

    @Override
    public int batchDeleteRelation(String ids) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getProductMaterialRelationDao().batchDeleteRelation(ids);
    }

    @Override
    public boolean checkRelationUnique(String productName, String materialName, Integer id, String companyName) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getProductMaterialRelationDao().checkRelationUnique(productName, materialName, id, companyName);
    }

    @Override
    public JSONObject findRelationsByProductName(String productName, String companyName) {
        DBContextHolder.setDbType("primary");
        List<ProductMaterialRelation> relationList = daoFacade.getProductMaterialRelationDao().findRelationsByProductName(productName, companyName);
        return toJSONObject(relationList);
    }

    @Override
    public JSONObject findProductNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName) {
        DBContextHolder.setDbType("primary");
        List<String> productNames = daoFacade.getProductMaterialRelationDao().findProductNamesByPrefix(prefix, pageNum, pageSize, companyName);
        int total = daoFacade.getProductMaterialRelationDao().getProductNamesCountByPrefix(prefix, companyName);
        JSONObject result = new JSONObject();
        result.put("data", productNames);
        result.put("total", total);
        return result;
    }

    private JSONObject toJSONObject(List<ProductMaterialRelation> relationList) {
        JSONObject result = new JSONObject();
        result.put("data", relationList);
        return result;
    }
}
