package com.nexsol.grape.dto.token.response;

import com.nexsol.grape.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
public class MemberLoginTokenResponseDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;

    public static MemberLoginTokenResponseDto from(TokenDto tokenDto){
        return new MemberLoginTokenResponseDto(tokenDto.getGrantType(), tokenDto.getAccessToken(), tokenDto.getRefreshToken(), tokenDto.getAccessTokenExpiresIn());
    }
}
