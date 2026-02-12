package com.xdmy.controller;

import com.xdmy.service.ServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.http.HttpServletRequest;

public class BaseController extends Controller {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ServiceFacade serviceFacade;

    /**
     * 从请求中获取公司名称
     * 优先从请求头中获取，其次从请求参数中获取，最后返回默认值
     */
    protected String getCompanyName(HttpServletRequest request) {
        // 从请求头中获取
        String companyName = request.getHeader("X-Company-Name");
        if (companyName != null && !companyName.isEmpty()) {
            try {
                // 解码公司名称，因为前端使用了encodeURIComponent编码
                return java.net.URLDecoder.decode(companyName, "UTF-8");
            } catch (Exception e) {
                // 如果解码失败，返回原始值
                return companyName;
            }
        }
        
        // 从请求参数中获取
        companyName = request.getParameter("companyName");
        if (companyName != null && !companyName.isEmpty()) {
            return companyName;
        }
        
        // 默认值
        return "";
    }
}
