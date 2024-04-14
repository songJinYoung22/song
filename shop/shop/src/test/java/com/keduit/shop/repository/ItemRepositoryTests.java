package com.keduit.shop.repository;

import com.keduit.shop.constant.ItemSellStatus;
import com.keduit.shop.entity.Item;
import com.keduit.shop.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTests {

    @Autowired
    ItemRepository itemRepository;

    // 쿼리DSL
    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("상품 저장 테스트") // 별 기능은 없고 가독성을 높이기 위함.
    // 좌측 하단 보면 어떤 메서드가 실행되었는지 확인가능.
    public void createItemTest() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상품 상세 설명(페이지)");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        //item.setRegDate(LocalDateTime.now()); // 아이탬 테이블이 생기는 순간의 시간.
        Item savedItem = itemRepository.save(item);

        System.out.println(savedItem);

    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNm() {
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품");

        for (Item item : itemList) {
            System.out.println(item);
            System.out.println();
        }

    }

    @Test
    @DisplayName("상품 저장 목록 테스트") // 뿌려줌.
    public void createItemListTest() { // 많이 생성해보자.

        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i* 1000);
            item.setItemDetail("상품 상세 설명(페이지)" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
           // item.setRegDate(LocalDateTime.now()); // 아이탬 테이블이 생기는 순간의 시간.
            Item savedItem = itemRepository.save(item);

            System.out.println(savedItem);
        }
    }

    @Test
    @DisplayName("상품명, 상품상세 설명 or 테스트")
    public void findByItemNmOrItemDetailTest() {
        List<Item> itemList =
                itemRepository.findByItemNmOrItemDetail(
                        "테스트 상품 1", "테스트 상품 상세 설명 10");
        for (Item item : itemList) {
            System.out.println(" 아이탬 입니다 : " + item);
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest() {
        List<Item> itemList =
                itemRepository.findItemNmByPriceLessThan(10001);
        for (Item item : itemList) {
            // find는 일단 모든 칼럼 값을 가져온다?
            System.out.println("가격이 만일원 이하인 아이탬 이름 목록 : " + item.getItemNm());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDescTests() {
        List<Item> itemList =
                itemRepository.findByPriceLessThanOrderByPriceDesc(18000);
        for (Item item : itemList) {
            System.out.println(item);

        }

    }

    @Test
    @DisplayName("@Query를 이용한 삼풍 조회 테스트")
    public void findByItemDetailTests() {
        List<Item> itemList = itemRepository.findByItemDetail("상품");
        for (Item item : itemList){
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("native 속성을 이용한 삼품조회 테스트")
    public void findByItemDetailByNativeTests(){
        List<Item> itemList = itemRepository.findByItemDetailByNative("상품 상세");
        for (Item item : itemList){
            System.out.println(item);
        }
    }
    
    // 동적 쿼리를 짤 떈 queryDSL을 많이 쓴다
    @Test
    @DisplayName("QueryDsl테스트")
    public void queryDslTest(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;

        List<Item> list = queryFactory
                .select(qItem)
                .from(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "상품" + "%"))
                .orderBy(qItem.price.desc())
                .fetch();
        for (Item item : list){
            System.out.println("item = " + item);
        }
    }
    
    @Test
    @DisplayName("상품 quertDsl 조회 테스트 2 (조건에 맞는 검색) ")
    public void queryDslTest2(){
        // 쿼리에 들어갈 조건을 ?
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QItem qItem = QItem.item; // 싱글톤패턴을 사용
        String itemDetail = "상품 상세";
        int price = 11000;
        String itemSellStat = "SELL";

        booleanBuilder.and(qItem.itemDetail.like("%" + itemDetail +"%"));
        booleanBuilder.and(qItem.price.gt(price));

        // 타임리프의 stringUtils
        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(qItem.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0,5);
        Page<Item> itemPageResult = itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements : " +
                itemPageResult.getTotalElements());
        List<Item> resultItemList = itemPageResult.getContent();
        for(Item resultItem : resultItemList){
            System.out.println(resultItem);
        }

    }
}