package com.nexsol.grape.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {

    private int status = 200;
    private String msg = "성공";
    private T data = null;
}
