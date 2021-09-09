package com.nexsol.grape.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ImportMemberDto {

    private String name;
    private String gender;
    private String birth;
    private String phone;
    private String importKey;
}
