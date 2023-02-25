package com.example.springwithonlyjava;

import com.example.springwithonlyjava.discount.FixDiscountPolicy;
import com.example.springwithonlyjava.member.MemberService;
import com.example.springwithonlyjava.member.MemberServiceImpl;
import com.example.springwithonlyjava.member.MemoryMemberRepsoitory;
import com.example.springwithonlyjava.order.OrderService;
import com.example.springwithonlyjava.order.OrderServiceImpl;

public class AppConfig {

    public MemberService memberService() {
        return new MemberServiceImpl(new MemoryMemberRepsoitory());
    }

    public OrderService orderService() {
        return new OrderServiceImpl(
                new MemoryMemberRepsoitory(),
                new FixDiscountPolicy()
                );
    }

}
