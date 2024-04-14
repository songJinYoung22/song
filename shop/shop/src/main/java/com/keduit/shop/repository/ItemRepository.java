package com.keduit.shop.repository;

import com.keduit.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;


// QuerydslPredicateExecutor : 조건에 맞는 조회를 위해 추가.
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>
    , ItemRepositoryCustom{
    // <> 이곳의 타입은 테이블의 이름과 프라이머리키의 타입이다.

    // 여기서 함수 명이 곧 쿼리 메소드. jpa 의 핵심 기능 중 하나다.
    // 레파지토리 인터페이스에 가단한 네이밍 룰을 이용하여 메소드를 작성하면 원하는 쿼리실행가능.
    // ppt를 확인하자.

    // queryDSL 은 동적쿼리를 다루기 위해 사용.
    List<Item> findByItemNm(String itemNm);

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    // 인티져 써야하는 이유?
    List<Item> findItemNmByPriceLessThan(Integer price);

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query("select i from Item i where i.itemDetail like" +
            " %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    @Query(value = "select * from item i where i.item_detail like " +
            "%:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);

}
