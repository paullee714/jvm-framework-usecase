package com.example.springwithonlyjava.member;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryMemberRepsoitory implements MemberRepository {
    private static ConcurrentHashMap<Long, Member> store = new ConcurrentHashMap<>();


    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }
}
