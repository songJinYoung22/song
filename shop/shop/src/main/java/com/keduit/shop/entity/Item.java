package com.keduit.shop.entity;

import com.keduit.shop.constant.ItemSellStatus;
import com.keduit.shop.dto.ItemFormDTO;
import com.keduit.shop.exception.OutOfStockException;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity{
// 테이블으름은 이 클레스의 이름과 같다 다르게 할 수도는 있음.

    // 여기서 칼럼명과 필드명을 다르게 한 이유는
    // 그냥 다른대서도 id 쓸텐데 헷갈리니까. 같아도 상관은 없음.
    // generatedValue 이부분은 1씩 증가하는 인덱스값
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id; // 상품코드!!

    // 낫널. 길이 50
    @Column(nullable = false, length = 50)
    private String itemNm; // 상품명!!



    // 칼럼 내용의 디폴트 값은 변수이름. 대문자일 경우 자동으로 언더바가 붙기때문에 소문자로 하는게 좋음.
    // 예. 칼럼 네임(직접 주기) 혹은 필드 명이 aBc 인 경우 db에선 a_bc로 받아들인다.
    // 그래서 필드명은 그냥 모두 소문자로 쓰이는 경우도 있음. _ 붙이기 싫으니까
    @Column(name="price", nullable = false)
    private int price; // 가격!!

    @Column(nullable = false)
    private int stockNumber; // 재고수량!!

    @Lob // lenth가 매우 긴 경우??
    @Column(nullable = false)
    private String itemDetail; // 상품상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  // 상품 판매 현황

    // 공부할 부분!
    public void updateItem(ItemFormDTO itemFormDTO){
        this.itemNm = itemFormDTO.getItemNm();
        this.price = itemFormDTO.getPrice();
        this.stockNumber = itemFormDTO.getStockNumber();
        this.itemDetail = itemFormDTO.getItemDetail();
        this.itemSellStatus = itemFormDTO.getItemSellStatus();
    }

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock < 0){
            throw new OutOfStockException("상품 재고가 부족합니다 (현재 재고 수량 : " + this.stockNumber + ")" );
        }
        this.stockNumber = restStock;
    }

    // 주문 취소 / 재고 증가
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }

    // 따로 만든거 상속받아 쓰면 됌.
/*    private LocalDateTime regDate; // 등록시간

    @CreationTimestamp // 계속 업데이트될 수 있기 때문에 붙임.
    private LocalDateTime updateTime; // 상품을 수정한 시간.*/

}
