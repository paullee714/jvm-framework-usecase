package com.example.springwithonlyjava.member;

public interface MemberService {

    void join(Member member);

    Member findMember(Long memberId);

}
