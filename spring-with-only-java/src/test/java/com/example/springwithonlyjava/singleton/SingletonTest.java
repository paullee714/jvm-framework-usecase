package com.example.springwithonlyjava.singleton;

import com.example.springwithonlyjava.AppConfig;
import com.example.springwithonlyjava.member.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SingletonTest {

    @Test
    @DisplayName("스프링 없는 DI 컨테이너")
    void pureContainer() {
        AppConfig appConfig = new AppConfig();

        MemberService memberService1 = appConfig.memberService();

        MemberService memberService2 = appConfig.memberService();

        System.out.println("memberService 1 = " + memberService1);
        System.out.println("memberService 2 = " + memberService2);

        Assertions.assertThat(memberService1).isNotSameAs(memberService2);

    }

    @Test
    @DisplayName("싱글톤 패턴을 적용한 객체 사용")
    void singletoneSerivceTest() {
        SingletonService singletonService1 = SingletonService.getInstance();
        SingletonService singletonService2 = SingletonService.getInstance();

        System.out.println("singletonService1 = " + singletonService1);
        System.out.println("singletonService2 = " + singletonService2);

        Assertions.assertThat(singletonService1).isSameAs(singletonService2);
        // same ==
        // eqaul .equals()
    }


    @Test
    @DisplayName("스프링 컨테이너와 싱글톤")
    void springContainer(){
//        AppConfig appConfig = new AppConfig();
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

//        MemberService memberService1 = appConfig.memberService();
        MemberService memberService1 = ac.getBean("memberService",MemberService.class);

//        MemberService memberService2 = appConfig.memberService();
        MemberService memberService2 = ac.getBean("memberService",MemberService.class);

        System.out.println("memberService 1 = " + memberService1);
        System.out.println("memberService 2 = " + memberService2);

        Assertions.assertThat(memberService1).isSameAs(memberService2);

    }
}
