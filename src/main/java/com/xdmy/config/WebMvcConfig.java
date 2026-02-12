package com.xdmy.config;

import com.xdmy.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private TokenInterceptor tokenInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Token拦截器，拦截所有请求，除了登录请求
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/admin/verifyLogin");
    }
}