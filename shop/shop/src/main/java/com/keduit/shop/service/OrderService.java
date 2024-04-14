package com.keduit.shop.service;

import com.keduit.shop.dto.OrderDTO;
import com.keduit.shop.dto.OrderHistDTO;
import com.keduit.shop.dto.OrderItemDTO;
import com.keduit.shop.entity.*;
import com.keduit.shop.repository.ItemImgRepository;
import com.keduit.shop.repository.ItemRepository;
import com.keduit.shop.repository.MemberRepository;
import com.keduit.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDTO orderDTO, String email) {
        // 주문할 상품 조회
        Item item = itemRepository.findById(orderDTO.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        // 현재 로그인한 회원의 이메일 정보를 이용하여 회원조회
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();
        // 주문 상품, 주문 수량을 이용하여 orderItem 엔티티를 생성.

        OrderItem orderItem = OrderItem.createOrderItem(item, orderDTO.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        // 회원정보, 주문 상품 리스트를 이용하여 주문 엔티티를 생성
        orderRepository.save(order);

        return order.getId();

    }

    @Transactional(readOnly = true) // 더티뭐시기
    public Page<OrderHistDTO> getOrderList(String email, Pageable pageable) {
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email); // pageable을 위해 토탈 카운트를 가져옴.

        List<OrderHistDTO> orderHistDTOs = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDTO orderHistDTO = new OrderHistDTO(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems){
                System.out.println(orderItem.getItem().getId().getClass()+"------------@@@@@@@@@@@@@@@@@@@@@@@@@@-------------------------");
                ItemImg itemImg = itemImgRepository
                        .findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                OrderItemDTO orderItemDTO = new OrderItemDTO(orderItem, itemImg.getImgUrl());// 여기에 넣을 값 그동안 만든것.
                orderHistDTO.addOrderItemDTO(orderItemDTO);
                orderHistDTO.getOrderStatus();
            }
            orderHistDTOs.add(orderHistDTO);
        }
        return new PageImpl<>(orderHistDTOs, pageable, totalCount);
    }

    // 주문 취소 전 이메일로 (로그인, 주문자) 일치여부 확인
    // 여기선 뭐 하는게 없고 그냥 이메일 대조만 해서 리턴값 보냄.
    @Transactional(readOnly = true) // 읽으려나보다
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow((EntityNotFoundException::new)); // findbyId 는 옵셔널타입이 디폴트이므로 null인경우 처리.필요.

        Member savemember = order.getMember();
        // StringUtils.equals 를 쓰는 이유는 null값이 들어가도 괜찮기에 쓴다

        if(!StringUtils.equals(curMember.getEmail(), savemember.getEmail())){
            return false;
        }
        return true;
    }

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        order.cancelOrder();

    }

    public Long orders(List<OrderDTO> orderDTOList, String email) {
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDTO orderDTO : orderDTOList){
            Item item = itemRepository.findById(orderDTO.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDTO.getCount());
            orderItemList.add(orderItem);
        }
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }
}