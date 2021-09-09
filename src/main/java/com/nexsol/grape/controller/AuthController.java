package com.nexsol.grape.controller;

import com.nexsol.grape.common.ApiResponseDto;
import com.nexsol.grape.domain.member.Member;
import com.nexsol.grape.dto.TokenDto;
import com.nexsol.grape.dto.member.request.MemberLoginRequestDto;
import com.nexsol.grape.dto.member.request.MemberSignupRequestDto;
import com.nexsol.grape.dto.member.response.MemberSignupResponseDto;
import com.nexsol.grape.dto.token.request.TokenReissueRequestDto;
import com.nexsol.grape.dto.token.response.MemberLoginTokenResponseDto;
import com.nexsol.grape.dto.token.response.TokenReissueResponseDto;
import com.nexsol.grape.service.AuthService;
import com.nexsol.grape.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(tags = "인증", value = "controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ApiOperation(value = "회원가입")
    public ApiResponseDto<MemberSignupResponseDto> signup(@Valid @RequestBody MemberSignupRequestDto memberSignupRequestDto){

        Member joinMember = authService.signup(memberSignupRequestDto);

        return new ApiResponseDto<MemberSignupResponseDto>(200, "회원가입 성공", MemberSignupResponseDto.from(joinMember));
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "성공시 jwt 토큰값을 쿠키 헤더에 넣어 반환합니다.")
    public ApiResponseDto<MemberLoginTokenResponseDto> login(@Valid @RequestBody MemberLoginRequestDto memberLoginRequestDto, HttpServletResponse response){

        TokenDto token = authService.login(memberLoginRequestDto, response);

        return new ApiResponseDto<MemberLoginTokenResponseDto>(200, "로그인 성공", MemberLoginTokenResponseDto.from(token));
    }

    @PostMapping("/reissue")
    @ApiOperation(value = "토큰 재발급")
    public ApiResponseDto<TokenReissueResponseDto> reissue(@RequestBody TokenReissueRequestDto tokenReissueRequestDto){

        TokenDto tokenDto = authService.reissue(tokenReissueRequestDto);

        return new ApiResponseDto(200, "토큰 재발급 성공", TokenReissueResponseDto.from(tokenDto));
    }
}
