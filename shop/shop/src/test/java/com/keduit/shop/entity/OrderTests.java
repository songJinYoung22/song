package com.keduit.shop.entity;

import com.keduit.shop.constant.ItemSellStatus;
import com.keduit.shop.repository.ItemRepository;
import com.keduit.shop.repository.MemberRepository;
import com.keduit.shop.repository.OrderItemRepository;
import com.keduit.shop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class OrderTests {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @PersistenceContext
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    public Item createItem(){
        Item item = new Item();
        item.setItemNm("order 상품");
        item.setPrice(10000);
        item.setItemDetail("order 상품 상세 설명(페이지)");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
      // item.setRegDate(LocalDateTime.now()); // 아이탬 테이블이 생기는 순간의 시간.

        return item; // 넣진않고 toString 만 사용
    }

    public Order createOrder(){
        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Item item = createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(10000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){

        Order order = new Order();

        for (int i = 0; i < 5; i++) {
            Item item = this.createItem();
            itemRepository.save(item); // 100개 db 저장

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(10000);
            orderItem.setOrder(order);
           // orderItem.setRegDate(item.getRegDate());

            // 아직 영속성 컨텍스트에 저장되지 않은 orderItem 엔티티를 order엔티티에 담아줌.
            order.getOrderItems().add(orderItem);
        }
        // order 엔티티를 저장하면서 강제로 flush를 호출하여 영속성 컨텍스트에 있는 객체들을 db에 반영.
        // oders 테이블 왜 제대로 들어가지 않았는지 확인할것!
//        orderRepository.saveAndFlush(order);
        orderRepository.save(order);

//        em.clear(); // 영속성 컨텍스트의 상태 초기화

        Order saveOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);

//        assertEquals(5, saveOrder.getOderItems().size());
    }

    @Test
    @DisplayName("고아 객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();

        order.getOrderItems().remove(0); // 첫번째 리스트 행을 제거한다.
        em.flush();
        // 트렌젠셕이라 실제 테이블에서 확인할 순 없지만 콘솔창에서 delete가 실행됫엇다는 것을 알 수 있음.
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder();

        // 첫번째 id를 가져오라
        Long orderItemId = order.getOrderItems().get(0).getId();
        System.out.println("================" + orderItemId); // 값 존재함
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);

//        System.out.println(orderItem+"--------------------------------------");

        // 한번 읽어보는 것?
        System.out.println("Order Class : " + orderItem.getOrder().getClass()); // orderItem 이 널값 인 이유.

        System.out.println("==================================");
        orderItem.getOrder().getOrderDate();
        System.out.println("==================================");
    }

    @Test
    public void orderItemListTest(){
        OrderItem orderItem = new OrderItem();

        System.out.println("----------------------"+ orderItemRepository.findAll());

    }
}
