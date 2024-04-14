package com.keduit.shop.controller;

import com.keduit.shop.dto.MemberFormDTO;
import com.keduit.shop.entity.Member;
import com.keduit.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/members") // 이곳으로 올때는 memberS S를 붙여서 불러야함. 헷갈릴 수 있음
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/new")
    public String memberForm(Model model){

        // DTO의 set,get을 사용할 수 있도록??
        model.addAttribute("memberFormDTO", new MemberFormDTO());
        return "member/memberForm";
    }

//    @PostMapping("/new")
//    public String memberForm(MemberFormDTO memberFormDTO){
//        Member member = Member.createMember(memberFormDTO, passwordEncoder);
//        memberService.saveMember(member);
//
//        return "redirect:/";
//    }

    @PostMapping("/new")
    public String newMember(@Valid MemberFormDTO memberFormDTO,
                            BindingResult bindingResult, Model model){
        // memberFormDTO 의 유효성 체크 결과를확인 -> 에러이면 다시 입력 폼을 리턴
        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }
        try{
            Member member = Member.createMember(memberFormDTO, passwordEncoder);
            memberService.saveMember(member);
        }catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginMember(){
        return "/member/memberLoginForm";
    }
    
    @GetMapping("/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg","아이디 혹은 비밀번호를 확인해주세요");

        // 에러 발생 시 여기로 와서 메시지를 에튜리부트 받은 후 다시 돌아간다!

        return "member/memberLoginForm";
    }
}
