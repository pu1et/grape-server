package com.nexsol.grape.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponse {

    private int status = 200;
    private String msg = "성공";
    private Object data = null;

}
