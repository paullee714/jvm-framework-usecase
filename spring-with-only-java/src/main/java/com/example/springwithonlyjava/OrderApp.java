package com.example.springwithonlyjava;

import com.example.springwithonlyjava.member.Grade;
import com.example.springwithonlyjava.member.Member;
import com.example.springwithonlyjava.member.MemberService;
import com.example.springwithonlyjava.member.MemberServiceImpl;
import com.example.springwithonlyjava.order.Order;
import com.example.springwithonlyjava.order.OrderService;
import com.example.springwithonlyjava.order.OrderServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderApp {

    public static void main(String[] args) {
//        AppConfig appConfig = new AppConfig();
//
//        MemberService memberService = appConfig.memberService();
//        OrderService orderService = appConfig.orderService();
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        OrderService orderService = ac.getBean("orderService", OrderService.class);

        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId,"itemA", 10000);

        System.out.println("order = " + order);
        System.out.println("order.calculatePrice = " + order.calculatePrice());

    }
}
