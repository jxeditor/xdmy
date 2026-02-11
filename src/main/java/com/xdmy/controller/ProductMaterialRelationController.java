package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.ProductMaterialRelation;
import com.xdmy.service.ServiceFacade;
import com.xdmy.service.inter.IProductMaterialRelationService;
import com.xdmy.utils.ErrorCode;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @Author xz
 * @Date 2026/2/10 11:00
 * @Description 产品与原材料关系Controller
 */
@RestController
@RequestMapping("/productMaterialRelation")
public class ProductMaterialRelationController extends BaseController {

    @RequestMapping("/findAllRelation")
    public String findAllRelation(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "productName", defaultValue = "") String productName) {
        JSONObject result = serviceFacade.getProductMaterialRelationService().findAllRelation(pageNum, pageSize, productName);
        return new JSONReturn(result).toString();
    }

    @RequestMapping("/addRelation")
    public String addRelation(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        String productName = params.get("productName");
        String materialName = params.get("materialName");
        String quantityStr = params.get("quantity");
        String isDefaultStr = params.get("isDefault");
        
        // 非空校验
        if (productName == null || productName.isEmpty() || materialName == null || materialName.isEmpty() || quantityStr == null) {
            return new JSONReturn(ErrorCode.PARAMS_ERROR).toString();
        }
        
        ProductMaterialRelation relation = new ProductMaterialRelation();
        relation.setProductName(productName);
        relation.setMaterialName(materialName);
        relation.setQuantity(Integer.parseInt(quantityStr));
        relation.setIsDefault(isDefaultStr != null ? Integer.parseInt(isDefaultStr) : 0);
        
        int result = serviceFacade.getProductMaterialRelationService().addRelation(relation);
        if (result > 0) {
            return new JSONReturn("success", "添加成功", 1).toString();
        } else {
            return new JSONReturn("error", "添加失败", 0).toString();
        }
    }

    @RequestMapping("/updateRelation")
    public String updateRelation(HttpServletRequest request) {
        HashMap<String, String> params = getRequestParam(request);
        String idStr = params.get("id");
        String productName = params.get("productName");
        String materialName = params.get("materialName");
        String quantityStr = params.get("quantity");
        String isDefaultStr = params.get("isDefault");
        
        // 非空校验
        if (idStr == null || productName == null || productName.isEmpty() || materialName == null || materialName.isEmpty() || quantityStr == null) {
            return new JSONReturn(ErrorCode.PARAMS_ERROR).toString();
        }
        
        ProductMaterialRelation relation = new ProductMaterialRelation();
        relation.setId(Integer.parseInt(idStr));
        relation.setProductName(productName);
        relation.setMaterialName(materialName);
        relation.setQuantity(Integer.parseInt(quantityStr));
        relation.setIsDefault(isDefaultStr != null ? Integer.parseInt(isDefaultStr) : 0);
        
        int result = serviceFacade.getProductMaterialRelationService().updateRelation(relation);
        if (result > 0) {
            return new JSONReturn("success", "修改成功", 1).toString();
        } else {
            return new JSONReturn("error", "修改失败", 0).toString();
        }
    }

    @RequestMapping("/deleteRelationById")
    public String deleteRelationById(@RequestParam(value = "id") Integer id) {
        int result = serviceFacade.getProductMaterialRelationService().deleteRelationById(id);
        if (result > 0) {
            return new JSONReturn("success", "删除成功", 1).toString();
        } else {
            return new JSONReturn("error", "删除失败", 0).toString();
        }
    }

    @RequestMapping("/batchDeleteRelation")
    public String batchDeleteRelation(@RequestParam(value = "ids") String ids) {
        int result = serviceFacade.getProductMaterialRelationService().batchDeleteRelation(ids);
        if (result > 0) {
            return new JSONReturn("success", "批量删除成功", 1).toString();
        } else {
            return new JSONReturn("error", "批量删除失败", 0).toString();
        }
    }

    @RequestMapping("/checkRelationUnique")
    public String checkRelationUnique(@RequestParam(value = "productName") String productName, @RequestParam(value = "materialName") String materialName, @RequestParam(value = "id", required = false) Integer id) {
        boolean isUnique = serviceFacade.getProductMaterialRelationService().checkRelationUnique(productName, materialName, id);
        if (isUnique) {
            return new JSONReturn("success", "关系唯一", 1).toString();
        } else {
            return new JSONReturn("error", "产品名称和原材料名称的组合已存在", 0).toString();
        }
    }

    @RequestMapping("/findRelationsByProductName")
    public String findRelationsByProductName(@RequestParam(value = "productName") String productName) {
        // 非空校验
        if (productName == null || productName.isEmpty()) {
            JSONObject result = new JSONObject();
            result.put("data", new java.util.ArrayList<>());
            return new JSONReturn(result).toString();
        }
        JSONObject result = serviceFacade.getProductMaterialRelationService().findRelationsByProductName(productName);
        return new JSONReturn(result).toString();
    }
}
