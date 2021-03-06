package com.example.springboothandlerinterceptorsimple.config;

import com.example.springboothandlerinterceptorsimple.common.MyLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class Configuration implements WebMvcConfigurer {

    @Autowired
    private MyLoggingInterceptor myLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(myLoggingInterceptor);
    }

}
