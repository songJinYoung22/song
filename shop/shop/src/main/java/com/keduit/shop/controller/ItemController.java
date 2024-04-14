package com.keduit.shop.controller;

import com.keduit.shop.dto.ItemFormDTO;
import com.keduit.shop.dto.ItemSearchDTO;
import com.keduit.shop.entity.Item;
import com.keduit.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/admin/item/new")
    public String itemForm(Model model) {

        model.addAttribute("itemFormDTO", new ItemFormDTO());

        return "item/itemForm";
    }

    @PostMapping("/admin/item/new")  // 바인딩리절트는 검증결과를 가지고잇음
    public String itemNew(@Valid ItemFormDTO itemFormDTO, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDTO.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDTO, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }
    // 여기서 부터 공부!!
    @PostMapping("/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDTO itemFormDTO,
                             BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDTO.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품이미지는 필수 입력입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDTO, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생했습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    @GetMapping({"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDTO itemSearchDTO,
                             @PathVariable("page") Optional<Integer> page,
                             Model model) {
        // 한페이지에 몇개씩 뿌리고 싶은가? 페이지가 존재할경우? 페이지를 가져오고? 페이지가 존재하지 않을경우?
        // 3개씩 뿌려라?
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDTO, pageable);
        System.out.println("----- items.getContent() : " + items.getContent());
        System.out.println("----- itemSearchDTO : " + itemSearchDTO);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDTO", itemSearchDTO);
        model.addAttribute("maxPage", 5);
        return "item/itemMng";
    }

    @GetMapping("/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDTO itemFormDTO = itemService.getItemDtl(itemId);
        model.addAttribute("item",itemFormDTO);
        return "item/itemDtl";
    }
}
