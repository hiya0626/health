package com.itheima.service;

import com.itheima.pojo.Member;

public interface MemberService {

    public Member findByTelephone(String telephone);

    public void add(Member member);
}
