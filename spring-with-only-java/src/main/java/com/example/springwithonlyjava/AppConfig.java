package com.example.springwithonlyjava;

import com.example.springwithonlyjava.discount.DiscountPolicy;
import com.example.springwithonlyjava.discount.FixDiscountPolicy;
import com.example.springwithonlyjava.discount.RateDiscountPolicy;
import com.example.springwithonlyjava.member.MemberService;
import com.example.springwithonlyjava.member.MemberServiceImpl;
import com.example.springwithonlyjava.member.MemoryMemberRepsoitory;
import com.example.springwithonlyjava.order.OrderService;
import com.example.springwithonlyjava.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // @Bean memberService -> new MemoryMemberRepository()
    // @Bean orderService -> new MemoryMemberRepository()

    @Bean
    public MemberService memberService() {
        System.out.println("AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemoryMemberRepsoitory memberRepository() {
        System.out.println("AppConfig.memberRepository");
        return new MemoryMemberRepsoitory();
    }

    @Bean
    public OrderService orderService() {
        System.out.println("AppConfig.orderService");
        return new OrderServiceImpl(
                memberRepository(),
                discountPolicy()
                );
    }

    @Bean
    public DiscountPolicy discountPolicy() {
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }

}
