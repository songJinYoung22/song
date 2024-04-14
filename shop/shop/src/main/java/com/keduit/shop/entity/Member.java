package com.keduit.shop.entity;

import com.keduit.shop.constant.Role;
import com.keduit.shop.dto.MemberFormDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
public class Member extends BaseEntity{

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true) // 중복허용 불가
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 테스트 인설트를 위한 편의 메서드?
    public  static Member createMember(MemberFormDTO memberFormDTO,
                                       PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDTO.getName());
        member.setEmail(memberFormDTO.getEmail());
        member.setAddress(memberFormDTO.getAddress());
        String password = passwordEncoder.encode(memberFormDTO.getPassword());
        member.setPassword(password);
        member.setRole(Role.USER);
        return member;
        // 이부분에서 db다 담을 member VO를 form으로 부터 받아 저장해 놓긴했지만
        // 아직 db에 save 즉 insert를 한것은 아니다.
    }
}
