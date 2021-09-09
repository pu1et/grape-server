package com.nexsol.grape.dto.member.response;

import com.nexsol.grape.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class MemberResponseDto {

    private Long id;
    private String name;
    private int gender;
    private Date birth;
    private String phone;

    public static MemberResponseDto from(Member member){
        return new MemberResponseDto(member.getId(), member.getName(), member.getGender(), member.getBirth(), member.getPhone());
    }
}
