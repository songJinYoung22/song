package com.keduit.shop.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class ItemDTO {

    private Long id;
    private String itemNm;
    private Integer price;
    private String itemDetail;
    private String sellStaCd;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

}
