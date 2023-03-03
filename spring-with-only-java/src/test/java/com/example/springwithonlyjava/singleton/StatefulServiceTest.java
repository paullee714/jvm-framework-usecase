package com.example.springwithonlyjava.singleton;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService1 = ac.getBean(StatefulService.class);
        StatefulService statefulService2 = ac.getBean(StatefulService.class);

        // Thread A :  A사용자가 10000원 주문
        statefulService1.order("userA", 10000);
        // Thread B :  B사용자가 20000원 주문
        statefulService2.order("userB", 20000);

        // ThreadA : 사용자 A 주문금액 조회
        int price = statefulService1.getPrice();
        System.out.println("prcie = " + price); // 10000원일까?

        Assertions.assertThat(statefulService1.getPrice()).isEqualTo(20000); // 공유 필드 때문에 나오는 문제
    }

    static class TestConfig {

        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }

}
