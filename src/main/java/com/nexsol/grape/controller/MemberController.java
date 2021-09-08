package com.nexsol.grape.controller;

import com.nexsol.grape.domain.Member;
import com.nexsol.grape.dto.member.MemberSignupRequestDto;
import com.nexsol.grape.common.ApiResponseDto;
import com.nexsol.grape.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("")
    public ApiResponseDto join(MemberSignupRequestDto memberSignupRequestDto){

        Member joinMember = memberService.join(memberSignupRequestDto);

        Map<String, Long> data = new HashMap<>();
        data.put("id", joinMember.getId());

        return new ApiResponseDto(200, "회원가입 성공", data);
    }
}
