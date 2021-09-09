package com.nexsol.grape.domain;

import com.nexsol.grape.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
@Entity
@Builder
@AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "refresh_token_key")
    private Long key; // id

    @Column(name = "refresh_token_value")
    private String value; // refresh Token 문자열

    public RefreshToken updateValue(String token){
        this.value = value;
        return this;
    }

    public static RefreshToken createRefreshToken(Authentication authentication, TokenDto tokenDto){
        return RefreshToken.builder()
                .key(Long.parseLong(authentication.getName()))
                .value(tokenDto.getRefreshToken())
                .build();
    }
}
