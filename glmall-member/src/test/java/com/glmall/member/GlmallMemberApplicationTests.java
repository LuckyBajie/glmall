package com.glmall.member;

import com.glmall.member.entity.MemberEntity;
import com.glmall.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class GlmallMemberApplicationTests {

    @Resource
    MemberService memberService;

    @Test
    void contextLoads() {

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setCity("北京");
        memberService.save(memberEntity);

        memberService.list().forEach((item)->{
            System.out.println(item);
        });
    }

}
