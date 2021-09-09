package com.nexsol.grape.dto.member.response;

import com.nexsol.grape.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Date;

@Getter
@AllArgsConstructor
public class MemberSignupResponseDto {

    private Long id;
    private String name;
    private int gender;
    private Date birth;
    private String phone;

    public static MemberSignupResponseDto from(Member member){
        return new MemberSignupResponseDto(member.getId(), member.getName(), member.getGender(), member.getBirth(), member.getPhone());
    }
}
