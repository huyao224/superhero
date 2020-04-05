package com.next.api.config;

import com.next.api.controller.interceptor.UserTokenInterceper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//虚拟路径映射类
@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //映射tomcat虚拟目录
        registry.addResourceHandler("/**")
                .addResourceLocations("file:E:temp/")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    @Bean
    public UserTokenInterceper userTokenInterceper(){
        return  new UserTokenInterceper();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器
        registry.addInterceptor(userTokenInterceper())
                .addPathPatterns("/user/modifyUserinfo")
                .addPathPatterns("/user/uploadFace");
    }
}
