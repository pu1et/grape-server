package com.nexsol.grape.service;

import com.nexsol.grape.controller.UserForm;
import com.nexsol.grape.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    static{
        System.setProperty("spring.config.location", "classpath:/application.yml,classpath:/ncp.yml,classpath:/test.yml");
    }

    @Autowired UserService userService;

    @Test
    @DisplayName("회원가입 시 정상적으로 사용자가 저장이 되어야 한다.")
    void join(){
        // give
        UserForm userForm = new UserForm("김이박", "010-0000-0000", "2000-02-20");

        // when
        User joinUser = userService.join(userForm);
        User findUser = userService.findOne(joinUser.getId()).get();

        // then
        assertThat(joinUser).isEqualTo(findUser);
    }

    @Test
    @DisplayName("존재하지 않는 회원은 예외가 터지게 된다.")
    void validateUser(){
        Long wrongId = 10000L;

        assertThrows(IllegalStateException.class, () -> userService.validateUser(wrongId));
    }
}