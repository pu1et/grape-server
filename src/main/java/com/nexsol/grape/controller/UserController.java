package com.nexsol.grape.controller;

import com.nexsol.grape.domain.User;
import com.nexsol.grape.response.ApiResponse;
import com.nexsol.grape.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ApiResponse join(UserForm userForm){

        User joinUser = userService.join(userForm);

        Map<String, Long> data = new HashMap<>();
        data.put("id", joinUser.getId());

        return new ApiResponse(200, "회원가입 성공", data);
    }
}
