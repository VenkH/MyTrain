package com.kyr.mytrain.member.config;

import com.kyr.mytrain.common.interceptor.LogInterceptor;
import com.kyr.mytrain.common.interceptor.MemberLoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MemberSpringMvcConfig implements WebMvcConfigurer {
    @Resource
    LogInterceptor logInterceptor;

    @Resource
    MemberLoginInterceptor memberInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(logInterceptor);

       registry.addInterceptor(memberInterceptor)
               .addPathPatterns("/**")
               .excludePathPatterns(
                       "/member-service/member/sendCode",
                       "/member-service/member/login"
               );
    }
}
