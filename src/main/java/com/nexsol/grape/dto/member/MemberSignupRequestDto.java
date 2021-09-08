package com.nexsol.grape.dto.member;

import com.nexsol.grape.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@AllArgsConstructor
@Getter @Setter
public class MemberSignupRequestDto {

    private String name;
    private String phone;
    private String birth;

    public Member toUser(){
        return Member.builder()
                .name(name)
                .phone(phone)
                .birth(stringToDate())
                .build();
    }

    private Date stringToDate(){
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            java.util.Date utilDate = dateFormat.parse(birth);
            return new Date(utilDate.getTime());

        }catch(ParseException p){
            throw new IllegalStateException("유효한 생년월일이 입력되지 않았습니다.");
        }
    }
}
