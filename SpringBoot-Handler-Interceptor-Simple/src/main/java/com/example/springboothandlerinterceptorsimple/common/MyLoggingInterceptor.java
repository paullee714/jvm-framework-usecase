package com.example.springboothandlerinterceptorsimple.common;


import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MyLoggingInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(MyLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (Objects.equals(request.getMethod(), "POST")) {
            Map<String, Object> inputMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);

            logger.info("요청 정보: " + inputMap);
            logger.info("요청 URL: " + request.getRequestURL());

            return true;
        } else {

            logger.info("요청 정보: " + request.getQueryString());
            logger.info("요청 URL: " + request.getRequestURL());
            return true;
        }
    }
}
