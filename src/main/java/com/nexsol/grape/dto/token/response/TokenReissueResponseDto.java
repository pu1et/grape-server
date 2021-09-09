package com.nexsol.grape.dto.token.response;

import com.nexsol.grape.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenReissueResponseDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;

    public static TokenReissueResponseDto from(TokenDto tokenDto){
        return new TokenReissueResponseDto(tokenDto.getGrantType(), tokenDto.getAccessToken(), tokenDto.getRefreshToken(), tokenDto.getAccessTokenExpiresIn());
    }
}
