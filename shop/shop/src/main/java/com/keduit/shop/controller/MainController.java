package com.keduit.shop.controller;

import com.keduit.shop.dto.ItemSearchDTO;
import com.keduit.shop.dto.MainItemDTO;
import com.keduit.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final ItemService itemService;

    @GetMapping("/")
    public String main(ItemSearchDTO itemSearchDTO,
                       Optional<Integer> page,
                       Model model){
        Pageable pageable = PageRequest.of(page.isPresent()? page.get() : 0, 6);

        Page<MainItemDTO> items = itemService.getMainItemPage(itemSearchDTO, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDTO",itemSearchDTO);
        model.addAttribute("maxPage",5);

        return "main";
    }
}
