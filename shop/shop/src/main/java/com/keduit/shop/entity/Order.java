package com.keduit.shop.entity;

//import javax.persistence.*; 이렇게하면 persistence 관련해 다 데려옴

import com.keduit.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태.

    // order 와 orderItem 은 1:다 의 연관 관계를 가짐.
    // 외례키가 orderItem에 있음으므로 연관 관계의 주인은 orderItem이 됨 --> order는 주인이 아니므로 mapped by 걸어줌.
    //cascade : 부모와 어떤 관계를 맺을지. 매우 중요함. 따로 공부. 지금 all 은 부모의 상태변화를 모두 물려받겠다.
    // 하나만 참조중?
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
    // 필드에 넣을 순 있지만 (당연히도?) 테이블에 추가되진 않는다. 그저 연결만 지어줄 뿐이다?
    // orphanRemoval = true : 참조 중 이 orderItem을 하나 참조?


    // orderItems에는 주문상품 정보를 추가함. orderItem 객체를 order 객체의 orderItems에 추가함.
    // Order엔티티와 OrderItem 엔티티가 양방향 참조관계이므로 orderItem객체에도
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);
        for (OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        // System.out.println(orderItems+"@@@@@@@@@@@@@@@@@@@@");
        return totalPrice;
    }

    public void cancelOrder(){

        // 주문 상태를 CANCEL로 변경.
        this.orderStatus = OrderStatus.CANCLE;

        // 주문 상품의 주문수량을 재고에서 증가시킴.
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;
}
