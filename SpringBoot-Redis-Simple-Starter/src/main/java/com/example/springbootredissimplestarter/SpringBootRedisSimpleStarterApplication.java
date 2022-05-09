package com.example.springbootredissimplestarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringBootRedisSimpleStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRedisSimpleStarterApplication.class, args);
    }

}
