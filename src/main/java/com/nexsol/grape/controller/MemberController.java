package com.nexsol.grape.controller;

import com.nexsol.grape.common.ApiResponseDto;
import com.nexsol.grape.domain.member.Member;
import com.nexsol.grape.dto.member.response.MemberResponseDto;
import com.nexsol.grape.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "사용자 확인/탈퇴")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    @ApiOperation(value = "사용자 확인", notes = "토큰 값으로 나의 정보 값 체크")
    public ApiResponseDto<MemberResponseDto> getMyMemberInfo(){
        Member memberInfo = memberService.getMyInfo();

        return new ApiResponseDto<MemberResponseDto>(200, "사용자 정보 확인 성공", MemberResponseDto.from(memberInfo));
    }

    @DeleteMapping("/me")
    @ApiOperation(value = "사용자 탈퇴", notes = "사용자의 선택에 의한 탈퇴")
    public ResponseEntity<Void> deleteMember(){
        memberService.leave();

        return ResponseEntity.noContent().build();
    }
}
