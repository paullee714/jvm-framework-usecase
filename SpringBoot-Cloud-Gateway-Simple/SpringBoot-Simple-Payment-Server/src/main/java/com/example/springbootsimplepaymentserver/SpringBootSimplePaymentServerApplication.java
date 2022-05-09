package com.example.springbootsimplepaymentserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SpringBootSimplePaymentServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSimplePaymentServerApplication.class, args);
    }

}
