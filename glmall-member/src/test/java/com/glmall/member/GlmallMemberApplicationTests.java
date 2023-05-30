package com.glmall.member;

import com.glmall.member.entity.MemberEntity;
import com.glmall.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

@SpringBootTest
class GlmallMemberApplicationTests {

    @Resource
    MemberService memberService;


    @Test
    void md5Test() {
        // 加盐
//        String s = DigestUtils.md5Hex("123456"+System.currentTimeMillis());
//        String s1 = Md5Crypt.md5Crypt("123456".getBytes(StandardCharsets.UTF_8), "$1$666999");
//        System.out.println(s);
//        System.out.println(s1);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        String encode1 = passwordEncoder.encode("123456");
        String encode2 = passwordEncoder.encode("123456");
        System.out.println(encode);
        System.out.println(encode1);
        System.out.println(encode2);
        boolean matches = passwordEncoder.matches("123456", "$2a$10$WX.tUzAVdc4D61T8MPMUV.4audFPNPCkT2PGSgzdvgiFpCOPNy9oy");
        System.out.println(matches);
    }

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
