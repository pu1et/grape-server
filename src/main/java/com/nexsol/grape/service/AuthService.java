package com.nexsol.grape.service;

import com.nexsol.grape.domain.RefreshToken;
import com.nexsol.grape.domain.member.Member;
import com.nexsol.grape.domain.member.enums.MemberStatus;
import com.nexsol.grape.dto.TokenDto;
import com.nexsol.grape.dto.member.ImportMemberDto;
import com.nexsol.grape.dto.member.request.MemberLoginRequestDto;
import com.nexsol.grape.dto.member.request.MemberSignupRequestDto;
import com.nexsol.grape.dto.token.request.TokenReissueRequestDto;
import com.nexsol.grape.jwt.TokenProvider;
import com.nexsol.grape.repository.MemberRepository;
import com.nexsol.grape.repository.RefreshTokenRepository;
import com.nexsol.grape.util.ImportUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ImportUtil importUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Transactional
    public Member signup(MemberSignupRequestDto memberSignupRequestDto){
        ImportMemberDto memberInfo = importUtil.getMemberInfo(memberSignupRequestDto.getImpUid());

        validateSignupUser(memberInfo.getPhone());
        Member member = Member.createMember(memberInfo, memberSignupRequestDto.getPassword(), passwordEncoder);
        memberRepository.save(member);
        return member;
    }

    @Transactional
    public TokenDto login(MemberLoginRequestDto memberLoginRequestDto, HttpServletResponse response){

        // 1. Login ID(phone)/PW를 기반으로 AuthenticationToken 생성
        final UsernamePasswordAuthenticationToken authenticationToken = memberLoginRequestDto.toAuthentication();

        Member findMember = memberService.findMember(memberLoginRequestDto.getPhone());
        validateLoginUser(findMember, memberLoginRequestDto);

        // 2. 실제로 검증 (사용자 id 체크)이 이루어지는 부분
        // authenticate 메서드가 실행이 될 때 CustomUserDetailsService에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenInfo(authentication);

        // 인증 정보 쿠키에 저장
        tokenProvider.createCookie(response, tokenDto.getAccessToken());

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.createRefreshToken(authentication, tokenDto);

        refreshTokenRepository.save(refreshToken);

        // 5. 토큰 발급
        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(TokenReissueRequestDto tokenReissueRequestDto){

        // 1. Refresh Token 검증
        if(!tokenProvider.validateToken(tokenReissueRequestDto.getRefreshToken())){
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member Id 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenReissueRequestDto.getAccessToken());

        // 3. 저장소에서 Member ID를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(Long.parseLong(authentication.getName()))
                .orElseThrow(() ->new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는 지 검사
        if(!refreshToken.getValue().equals(tokenReissueRequestDto.getRefreshToken())){
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto jwtToken = tokenProvider.generateTokenInfo(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(jwtToken.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return jwtToken;
    }

    private void validateSignupUser(String phone){
        if(memberRepository.existsByPhone(phone)){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    private void validateLoginUser(Member member, MemberLoginRequestDto memberRequestDto){
        if(member.getStatus() != MemberStatus.ACTIVE){
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }

        if (!passwordEncoder.matches(memberRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalStateException("비밀번호가 맞지 않습니다.");
        }
    }
}
