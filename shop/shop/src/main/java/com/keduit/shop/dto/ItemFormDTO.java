package com.keduit.shop.dto;

import com.keduit.shop.constant.ItemSellStatus;
import com.keduit.shop.entity.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ItemFormDTO {

    private Long id;

    @NotBlank(message = "상품명은 필수 입력입니다.")
    private String itemNm;
    @NotNull(message = "가격은 필수 입력입니다.")
    private Integer price;
    @NotBlank(message = "상품 상세 내용은 필수 입력입니다.")
    private String itemDetail;

    @NotNull(message = "재고수량은 필수 입력입니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    private List<ItemImgDTO> itemImgDTOList = new ArrayList<>();

    private List<Long> itemImgIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    // ItemFormDTO -> Item
    public Item createItem(){
        return modelMapper.map(this, Item.class);
    }

    //  Item -> ItemFormDTO
    public static ItemFormDTO of(Item item){
        return modelMapper.map(item, ItemFormDTO.class);
    }
}
