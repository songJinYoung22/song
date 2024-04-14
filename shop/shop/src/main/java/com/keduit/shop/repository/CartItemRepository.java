package com.keduit.shop.repository;

import com.keduit.shop.dto.CartDetailDTO;
import com.keduit.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndItemId(long cartId, long itemId);

    // JPQL이란 ?관계형 데이터베이스 테이블이 아닌 엔티티 객체에 대한 쿼리를 작성할 때 사용됨.
    //엔티티 클래스와 필드를 대상으로 쿼리를 작성한다.
    //엔티티 간의 관계와 상속을 고려하여 쿼리를 작성할 수 있다.
    //데이터베이스 벤더에 독립적이다. (JPA 구현체에 따라 지원되는 SQL이 달라지지 않는다)
    //객체 지향적인 쿼리 작성을 지원하여 유연한 쿼리 작성이 가능하다.
    //정적 쿼리와 동적 쿼리를 모두 작성할 수 있다.


    // JPQL에서 연관 관계 지연로딩 LAZY 로 설정한 경우 엔티티에 매핑된 다른 엔티티를 조회할 때 추가적으로
    // 쿼리문이 실행되는데 이때 성능 최적화를 위해 DTO의 생성자를 이용하여 반환값으로 DTO객체를 생성할 수 있다
    //
    // CartDetailDTO의 생성자를 이용하여 DTO를 반환: 패키지명을 포함한 DTO의 이름을 기술.
    // 주의사항 : 생성자의 파라미터 순서는 꼭 지켜야함.
    @Query("select new com.keduit.shop.dto.CartDetailDTO(ci.id, i.itemNm, i.price, ci.count, im.imgUrl)"+
        "from CartItem ci, ItemImg im "+
        "join ci.item i "+
        "where ci.cart.id = :cartId "+
        "and im.item.id = ci.item.id "+
        "and im.repimgYn = 'Y' " +
        "order by ci.regTime desc")

    //  1. 파라미터 1개
//  2. 파라미터 이름과 매핑명이  동일
//  3. JPA 2.0 이상
//  위의 세가지 조건이 만족할 때 @Param 는 생략 가능  @Param("cartId") Long cartId

    List<CartDetailDTO> findCartDetailDTOList(Long cartId);
}

