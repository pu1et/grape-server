package com.nexsol.grape.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponseDto {

    private int status = 200;
    private String msg = "성공";
    private Object data = null;

}
