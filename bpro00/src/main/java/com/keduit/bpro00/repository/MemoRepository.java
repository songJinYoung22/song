package com.keduit.bpro00.repository;

import com.keduit.bpro00.entity.Memo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MemoRepository extends JpaRepository<Memo, Long> {
                                // 여기서 JpaRepository<Memo, Long> 의 타입은 엔티티 명, id 타입.

        // jsp 함수 (문법) 사용의 예. 함수 명만 으로 쿼리문법의 기능을 가진다.
    // 다만 복잡한 쿼리문은 짜기 불리하므로 그런 경우엔 직접 쿼리문을 짤 수도 있다.
    // 이 jsp 문법을 잘 다루기 전에 sql 문법 자체를 먼저 잘 알아야 한다.

    // 간단한 crud는 이미 만들어져 있지만 커스텀 db접근 할경우 아래처럼
    // jsp 문법을 사용해 메서드 시그니처 생성.
    List<Memo> findByMnoBetweenOrderByMnoDesc(Long from, Long to);

    Page<Memo> findByMnoBetween(Long from, Long to, Pageable pageable);

    void deleteMemoByMnoLessThan(Long num);
}
