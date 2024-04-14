package com.keduit.shop.controller;

import com.keduit.shop.dto.MemberFormDTO;
import com.keduit.shop.entity.Member;
import com.keduit.shop.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTests {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(String email, String password){

        MemberFormDTO memberFormDTO = new MemberFormDTO();
        memberFormDTO.setEmail(email);
        memberFormDTO.setName("song");
        memberFormDTO.setAddress("paju");
        memberFormDTO.setPassword(password);

//        위에걸로 만들고.
        Member member = Member.createMember(memberFormDTO,passwordEncoder);

        // db에 저장.
        return memberService.saveMember(member);
    }

    // 트렌젝셔널때문에 db에 직접 담기지는 않음.
    @Test
    @DisplayName("로그인 성공")
    public void loginSuccessTest() throws Exception{
        String email = "123123@naver.com";
        String password = "12345678";
        this.createMember(email, password);

        mockMvc.perform(formLogin().userParameter("email")
                .loginProcessingUrl("/members/login")
                .user(email).password(password))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());

    }

    // 트렌젝셔널때문에 db에 직접 담기지는 않음.
    @Test
    @DisplayName("로그인 실패 테스트")
    public void loginFailTest() throws Exception{
        String email = "123123@naver.com";
        String password = "12345678";
        this.createMember(email, password);

        mockMvc.perform(formLogin().userParameter("email")
                        .loginProcessingUrl("/members/login")
                        .user(email).password(password+123))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
                                                    // unauthenticated() 위에서 이부분만 바꿈. 실패되길 기대함.
    }
}
