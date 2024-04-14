package com.keduit.shop.service;

import com.keduit.shop.dto.CartDetailDTO;
import com.keduit.shop.dto.CartItemDTO;
import com.keduit.shop.dto.CartOrderDTO;
import com.keduit.shop.dto.OrderDTO;
import com.keduit.shop.entity.Cart;
import com.keduit.shop.entity.CartItem;
import com.keduit.shop.entity.Item;
import com.keduit.shop.entity.Member;
import com.keduit.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final OrderService orderService;

    // 장바구니 추가하기
    public Long addCart(CartItemDTO cartItemDTO, String email) {

        //  장바구니 넣을 상품 조회
        Item item = itemRepository.findById(cartItemDTO.getItemId())
                .orElseThrow(EntityExistsException::new);

        // 로그인 한 회원 엔티티 조회
        Member member = memberRepository.findByEmail(email);

        // 현재 회원의 장바구니가 있는지 조회
        Cart cart = cartRepository.findByMemberId(member.getId());

        // 장바구니가 없으면 생성, 있으면 수량 증가 시킴.
        if (cart == null) { // 장바구니에 상품이 있는지 없는지 확인. 없을 시 장바구니 만들어주기.
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }
        // 장바구니에 상품이 들어 있는지 확인 후 중복되면 수량 add, 없으면 장바구니 상품을 추가.
        CartItem saveCartItem =
                cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
        if (saveCartItem != null) { // 장바구니에 상품이 이미 "동일한?" 있는 상태라면?
            saveCartItem.addCount(cartItemDTO.getCount());
            return saveCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDTO.getCount());

            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<CartDetailDTO> getCartList(String email) {

        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            return cartDetailDTOList;
        }
        cartDetailDTOList = cartItemRepository.findCartDetailDTOList(cart.getId());
        return cartDetailDTOList;
    }

    // 현재 로그인 한 회원과 장바구니에 상품을 저장한 회원을 체크하여 같으면 true 다르면 false
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member member = memberRepository.findByEmail(email);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(member.getEmail(), savedMember.getEmail())){
            return false;
        }
        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDTO> cartOrderDTOList, String email) {

        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (CartOrderDTO cartOrderDTO : cartOrderDTOList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDTO.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setItemId(cartItem.getItem().getId());
            orderDTO.setCount(cartItem.getCount());
            orderDTOList.add(orderDTO);
        }
        // 주문 로직을 호출하여 처리.
        Long orderId = orderService.orders(orderDTOList, email);

        // 주문이 완료된 상품을 장바구니에서 삭제하기.
        for(CartOrderDTO cartOrderDTO: cartOrderDTOList){
            CartItem cartItem =cartItemRepository.findById(cartOrderDTO.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }
        return orderId;
    }
}