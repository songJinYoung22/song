package com.keduit.shop.dto;

import com.keduit.shop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderItemDTO {

    private String itemNm;

    private int count;
    private int orderPrice;
    private String imgUrl;

/*    public OrderItemDTO(OrderItem orderItem,String imgUrl) {
        this.itemNm = itemNm;
        this.count = count;
        this.orderPrice = orderPrice;
        this.imgUrl = imgUrl;
    }*/

    // 생성자를 호출하면 파라미터를 orderItem 객체와 ingUrl 자체로 받음.
    // 그러므로 이건 화면에 뿌려주기 위한 전용 DTO? 뭔소리?
    //
public OrderItemDTO(OrderItem orderItem, String imgUrl){
    this.itemNm = orderItem.getItem().getItemNm();
    this.count = orderItem.getCount();
    this.orderPrice = orderItem.getOrderPrice();
    this.imgUrl = imgUrl;
}

}
