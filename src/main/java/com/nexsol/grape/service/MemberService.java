package com.nexsol.grape.service;

import com.nexsol.grape.domain.RefreshToken;
import com.nexsol.grape.domain.member.enums.MemberStatus;
import com.nexsol.grape.dto.TokenDto;
import com.nexsol.grape.dto.token.request.TokenReissueRequestDto;
import com.nexsol.grape.dto.member.ImportMemberDto;
import com.nexsol.grape.dto.member.request.MemberLoginRequestDto;
import com.nexsol.grape.dto.member.request.MemberSignupRequestDto;
import com.nexsol.grape.domain.member.Member;
import com.nexsol.grape.jwt.TokenProvider;
import com.nexsol.grape.repository.MemberRepository;
import com.nexsol.grape.repository.RefreshTokenRepository;
import com.nexsol.grape.util.ImportUtil;
import com.nexsol.grape.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 현재 SecurityContext 에 있는 유저 정보 가져오기(토큰에 있는 유저 정보)
    @Transactional(readOnly = true)
    public Member getMyInfo(){
        return memberRepository.findByPhone(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> new IllegalStateException("로그인 유저 정보가 없습니다."));
    }

    @Transactional(readOnly = true)
    public Member findMember(String phone){
        return memberRepository.findByPhone(phone)
                .orElseThrow( () -> new IllegalStateException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public void leave(){
        Member member = getMyInfo();
        member.setStatus(MemberStatus.INACTIVE);
    }
}
