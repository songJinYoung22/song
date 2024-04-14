package com.keduit.shop.entity;

import com.keduit.shop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@SpringBootTest
@Transactional
public class MemberTests {

  @Autowired
  MemberRepository memberRepository;

  @PersistenceContext
  EntityManager em;

  @Test
  @DisplayName("Auditing테스트")
  @WithMockUser(username="gildong", roles="USER")   // role이 user인 사용자 gildong이 로그인했다고 가정
  public void auditingTest(){
    Member member = new Member();
    memberRepository.save(member);

    em.flush();
    em.clear();

    Member tmember = memberRepository.findById(member.getId())
        .orElseThrow(EntityNotFoundException::new);

    System.out.println("등록일: " + tmember.getRegTime());
    System.out.println("수정일: " + tmember.getUpdateTime());
    System.out.println("등록자: " + tmember.getCreatedBy());
    System.out.println("수정자: " + tmember.getModifiedBy());
  }
}
