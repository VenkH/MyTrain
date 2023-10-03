package com.kyr.mytrain.business.config;

import com.kyr.mytrain.common.interceptor.LogInterceptor;
import com.kyr.mytrain.common.interceptor.MemberLoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BusinessSpringMvcConfig implements WebMvcConfigurer {
    @Resource
    LogInterceptor logInterceptor;

    @Resource
    MemberLoginInterceptor memberLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(logInterceptor);
       registry.addInterceptor(memberLoginInterceptor);
    }
}
