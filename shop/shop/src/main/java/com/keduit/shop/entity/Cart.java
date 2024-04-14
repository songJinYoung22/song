package com.keduit.shop.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends BaseEntity{

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EAGER : 즉시 로딩 전략 = cart를 읽을때 member를 바로 읽어옴.
    // one to one, one to many 는 EAGER 전략이 default 임
    @OneToOne(fetch = FetchType.LAZY) // 1:1 대응. 카트와 회원의 관계.
    @JoinColumn(name="member_id") // 조인. 포링키? 외례키 지정
    private Member member; // 1:1 대응이기에 행이 하나밖에 들어가지 못하는가?

    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }
}
