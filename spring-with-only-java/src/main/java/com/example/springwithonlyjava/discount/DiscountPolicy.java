package com.example.springwithonlyjava.discount;

import com.example.springwithonlyjava.member.Member;

public interface DiscountPolicy {

    int discount(Member member, int price);

}
