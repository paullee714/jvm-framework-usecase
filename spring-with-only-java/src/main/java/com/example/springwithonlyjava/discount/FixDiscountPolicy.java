package com.example.springwithonlyjava.discount;

import com.example.springwithonlyjava.member.Grade;
import com.example.springwithonlyjava.member.Member;

public class FixDiscountPolicy implements DiscountPolicy{

    private int discountFixAmount = 1000;


    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP){
            return discountFixAmount;
        }else{
            return 0;
        }
    }
}
