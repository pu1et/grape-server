package com.nexsol.grape.service;

import com.nexsol.grape.dto.member.MemberSignupRequestDto;
import com.nexsol.grape.domain.Member;
import com.nexsol.grape.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member join(MemberSignupRequestDto memberSignupRequestDto){
        Member member = memberSignupRequestDto.toUser();
        memberRepository.save(member);
        return member;
    }

    @Transactional
    public Optional<Member> findOne(Long userId){
        return memberRepository.findById(userId);
    }

    /**
     * 회원 검증
     * @param id
     */
    public void validateUser(Long id){
        memberRepository.findById(id)
                .orElseThrow( () -> {
                    throw new IllegalStateException("존재하지 않는 회원입니다.");
                });
    }
}
