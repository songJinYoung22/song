package com.keduit.bpro00.entity;

import lombok.*;

import javax.persistence.*;

// 부트에선 엔티티가 곧 테이블 생성이다.
@Entity  // VO임을 선언.
@Table(name="t_memo") // 테이블 네임.
@ToString
@Getter
@Builder // 다른 클레스에서 Memo 클래스를 초기화할 필요 없이 바로 필드 접근 가능.
@AllArgsConstructor // 모든 필드를 파라미터로 쓰는 생성자 생성.
@NoArgsConstructor // 디폴트 생성자 생성.
public class Memo {

  /* @Id : primary key
  *  @GeneratedValue(strategy = GenerationType. : 키 생성 전략
  *   IDENTITY : mysql, mariadb -> auto increment
  *   SEQUENCE : oracle, h2 -> 시퀀스 테이블 이용
  *   AUTO : JPA구현체(부트 Hibernate)가 생성 방식을 결정
  * */

  @Id // 프라이머리키 설정.
  @GeneratedValue(strategy = GenerationType.IDENTITY) // 1씩 늘어나도록 인덱스 설정. 시퀀스.
  private Long mno;

  // nullable = false : 낫널 길이  기본값은 255
  @Column(length = 200, nullable = false)
  private String memoText;

}