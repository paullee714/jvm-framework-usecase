package com.example.springbootsimpleorderserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SpringBootSimpleOrderServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSimpleOrderServerApplication.class, args);
    }

}
