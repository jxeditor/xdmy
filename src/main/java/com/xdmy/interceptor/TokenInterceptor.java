package com.xdmy.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.service.inter.impl.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Token验证拦截器
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private AdminService adminService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 登录请求不需要验证Token
        if (request.getRequestURI().equals("/admin/verifyLogin")) {
            return true;
        }

        // 从请求头中获取Token
        String token = request.getHeader("X-Token");
        if (token == null || token.isEmpty()) {
            // 从请求参数中获取Token
            token = request.getParameter("token");
        }

        // 验证Token是否有效
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            JSONObject result = new JSONObject();
            result.put("code", 401);
            result.put("message", "Token不能为空");
            out.write(result.toString());
            out.flush();
            out.close();
            return false;
        }

        // 验证Token是否存在于数据库中
        com.xdmy.domain.User user = adminService.getUserByToken(token);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            JSONObject result = new JSONObject();
            result.put("code", 401);
            result.put("message", "Token无效或已过期");
            out.write(result.toString());
            out.flush();
            out.close();
            return false;
        }

        // Token验证通过，将用户信息存储到请求中
        request.setAttribute("user", user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {
        // 处理完请求后调用
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) throws Exception {
        // 整个请求完成后调用
    }
}