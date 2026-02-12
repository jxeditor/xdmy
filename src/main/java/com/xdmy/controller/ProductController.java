package com.xdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Product;
import com.xdmy.utils.JSONReturn;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @Author xz
 * @Date 2026/2/10 12:00
 * @Description 产品Controller
 */
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @RequestMapping("/findAllProduct")
    public String findAllProduct(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                 @RequestParam(value = "productName", defaultValue = "") String productName,
                                 javax.servlet.http.HttpServletRequest request) {
        String companyName = getCompanyName(request);
        JSONObject result = serviceFacade.getProductService().findAllProduct(pageNum, pageSize, productName, companyName);
        return new JSONReturn(result).toString();
    }

    @RequestMapping("/addProduct")
    public String addProduct(@RequestBody Product product,
                           javax.servlet.http.HttpServletRequest request) {
        String companyName = getCompanyName(request);
        product.setCompany_name(companyName);
        int result = serviceFacade.getProductService().addProduct(product, companyName);
        if (result > 0) {
            return new JSONReturn("success", "添加成功", 1).toString();
        } else {
            return new JSONReturn("error", "添加失败", 0).toString();
        }
    }

    @RequestMapping("/updateProduct")
    public String updateProduct(@RequestBody Product product,
                              javax.servlet.http.HttpServletRequest request) {
        String companyName = getCompanyName(request);
        product.setCompany_name(companyName);
        int result = serviceFacade.getProductService().updateProduct(product, companyName);
        if (result > 0) {
            return new JSONReturn("success", "修改成功", 1).toString();
        } else {
            return new JSONReturn("error", "修改失败", 0).toString();
        }
    }

    @RequestMapping("/deleteProductById")
    public String deleteProductById(@RequestParam(value = "id") Integer id,
                                   javax.servlet.http.HttpServletRequest request) {
        int result = serviceFacade.getProductService().deleteProductById(id);
        if (result > 0) {
            return new JSONReturn("success", "删除成功", 1).toString();
        } else {
            return new JSONReturn("error", "删除失败", 0).toString();
        }
    }

    @RequestMapping("/batchDeleteProduct")
    public String batchDeleteProduct(@RequestParam(value = "ids") String ids,
                                   javax.servlet.http.HttpServletRequest request) {
        int result = serviceFacade.getProductService().batchDeleteProduct(ids);
        if (result > 0) {
            return new JSONReturn("success", "批量删除成功", 1).toString();
        } else {
            return new JSONReturn("error", "批量删除失败", 0).toString();
        }
    }

    @RequestMapping("/findProductById")
    public String findProductById(@RequestParam(value = "id") Integer id,
                                 javax.servlet.http.HttpServletRequest request) {
        Product product = serviceFacade.getProductService().findProductById(id);
        JSONObject result = new JSONObject();
        result.put("data", product);
        return new JSONReturn(result).toString();
    }

    @RequestMapping("/findProductNamesByPrefix")
    public String findProductNamesByPrefix(@RequestBody HashMap<String, Object> request) {
        String prefix = (String) request.getOrDefault("prefix", "");
        String companyName = (String) request.getOrDefault("companyName", "");
        int pageNum = request.containsKey("pageNum") ? Integer.parseInt(request.get("pageNum").toString()) : 1;
        int pageSize = request.containsKey("pageSize") ? Integer.parseInt(request.get("pageSize").toString()) : 10;
        JSONObject result = serviceFacade.getProductService().findProductNamesByPrefix(prefix, pageNum, pageSize, companyName);
        return new JSONReturn(result).toString();
    }
}
