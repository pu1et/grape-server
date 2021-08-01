package com.nexsol.grape.service;

import com.nexsol.grape.controller.UserForm;
import com.nexsol.grape.domain.User;
import com.nexsol.grape.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User join(UserForm userForm){
        User user = userForm.toUser();
        userRepository.save(user);
        return user;
    }

    @Transactional
    public Optional<User> findOne(Long userId){
        return userRepository.findById(userId);
    }

    /**
     * 회원 검증
     * @param id
     */
    public void validateUser(Long id){
        userRepository.findById(id)
                .orElseThrow( () -> {
                    throw new IllegalStateException("존재하지 않는 회원입니다.");
                });
    }
}
