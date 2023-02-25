package com.example.springwithonlyjava;

import com.example.springwithonlyjava.discount.DiscountPolicy;
import com.example.springwithonlyjava.discount.FixDiscountPolicy;
import com.example.springwithonlyjava.member.MemberService;
import com.example.springwithonlyjava.member.MemberServiceImpl;
import com.example.springwithonlyjava.member.MemoryMemberRepsoitory;
import com.example.springwithonlyjava.order.OrderService;
import com.example.springwithonlyjava.order.OrderServiceImpl;

public class AppConfig {

    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    private static MemoryMemberRepsoitory memberRepository() {
        return new MemoryMemberRepsoitory();
    }

    public OrderService orderService() {
        return new OrderServiceImpl(
                memberRepository(),
                discountPolicy()
                );
    }

    public DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }

}
