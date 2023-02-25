package com.example.springwithonlyjava.order;

import com.example.springwithonlyjava.discount.DiscountPolicy;
import com.example.springwithonlyjava.discount.FixDiscountPolicy;
import com.example.springwithonlyjava.member.Member;
import com.example.springwithonlyjava.member.MemberRepository;
import com.example.springwithonlyjava.member.MemoryMemberRepsoitory;

public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository = new MemoryMemberRepsoitory();
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
