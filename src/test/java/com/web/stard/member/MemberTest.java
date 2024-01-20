package com.web.stard.member;

import com.web.stard.domain.Member;
import com.web.stard.service.MemberService;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootTest
public class MemberTest {

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("비밀번호 테스트")
    public void pwTest() {
        String pw = "final";

        String encoded_pw;


    }



    @Test
    @DisplayName("아이디 찾기")
    public void findId() {

        String email = "final";
        String phone = "01011111111";
//        Member result = memberService.findId(email, phone);
//
////        Assert.assertEquals(result, null);
//        Assert.assertEquals(result.getId(), "final");
//
//        if (result == null)
//            System.out.println("회원 정보가 없습니다.");
//        else
//            System.out.println("아이디는 " + result.getId() + "입니다.");
    }
}
