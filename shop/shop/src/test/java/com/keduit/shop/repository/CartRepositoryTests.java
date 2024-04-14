package com.keduit.shop.repository;


import com.keduit.shop.dto.MemberFormDTO;
import com.keduit.shop.entity.Cart;
import com.keduit.shop.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class CartRepositoryTests {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    // 쿼리 DSL
    @PersistenceContext
    EntityManager em;

    public Member createMember() {

        MemberFormDTO memberFormDTO = new MemberFormDTO();
        memberFormDTO.setEmail("rhaehfdl99997@naver.com");
        memberFormDTO.setName("송진영");
        memberFormDTO.setAddress("파주시");
        memberFormDTO.setPassword("12345678");

        return Member.createMember(memberFormDTO,passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 entity mapping 조회 테스트")
    public void findCartAndMemberTest(){
        Member member = createMember(); // 위 메서드 활용해 하나 만듬.
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        // db에 실제 반영. 하지만 트렌젹션이 있기에 최종 완료 후 다시 삭제.
        // 엔티티 메니저가 db에 반영.
        // 엔티티메니저를 왜 썻는가? 플러쉬와 클리어의 기능 확인.
        em.flush();
        em.clear();

        Cart saveCart = cartRepository.findById(cart.getId())
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(saveCart.getMember().getId(), member.getId());
    }
}
