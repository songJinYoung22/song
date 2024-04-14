package com.keduit.shop.dto;

import com.keduit.shop.constant.OrderStatus;
import com.keduit.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class OrderHistDTO {

    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;

    // 기본 세터 아님. 체크.
    public OrderHistDTO(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }

    // 주문 상품 리스트
    private List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

    // orderItemDTO를 주문 상품 리스트에 추가
    public void addOrderItemDTO(OrderItemDTO orderItemDTO){
        orderItemDTOList.add(orderItemDTO);
    }
}
