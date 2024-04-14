package com.keduit.shop.service;

import com.keduit.shop.dto.MemberFormDTO;
import com.keduit.shop.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
//@Transactional
public class MemberServiceTests {
    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){
        MemberFormDTO memberFormDTO = new MemberFormDTO();
        memberFormDTO.setEmail("tesdt@song.com");
        memberFormDTO.setName("song");
        memberFormDTO.setAddress("paju");
        memberFormDTO.setPassword("1234");

        // 여기서 Member은 entity의  Member 이다. 만들어놨던것.
        return Member.createMember(memberFormDTO, passwordEncoder);
    }
    
    @Test
    @DisplayName("회원 가입 테스트")
    // 이부분은 지금까지의 메서드가 문제 없다면 (이메일 중복이 없다면) 항상 true인 메서드이다
    // db에 저장된 정보와 (saveMember) db에 담기 전 VO인 member.get를 비교.
    // saveMember 를 통과했다면 반드시 같을 수 밖에 없음.
    public void saveMemberTest(){
        Member member = createMember();
        Member saveMember = memberService.saveMember(member);

        // 등록된 엔티티와 등록 전? 멤버가 제대로 맞는지
        assertEquals(member.getEmail(),saveMember.getEmail());
        assertEquals(member.getName(),saveMember.getName());
        assertEquals(member.getAddress(),saveMember.getAddress());
        assertEquals(member.getPassword(),saveMember.getPassword());
        assertEquals(member.getRole(),saveMember.getRole());
    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateMemberTest(){
        Member member1 = createMember();
        Member member2 = createMember();
        memberService.saveMember(member1);

        // 오류가 발생 시 이메일이 동일
        Throwable e = assertThrows(IllegalStateException.class,
                () -> {memberService.saveMember(member2);});
        assertEquals("이미 가입된 회원입니다.", e.getMessage());
    }
}

    