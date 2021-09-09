package com.nexsol.grape.dto.token.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class TokenReissueRequestDto {

    @NotBlank(message = "기존 accessToken 값이 있어야 됩니다.")
    @ApiModelProperty(value = "액세스 토큰", required = true, example = "asdfg")
    private String accessToken;

    @NotBlank(message = "기존 refreshToken 값이 있어야 됩니다.")
    @ApiModelProperty(value = "리프레쉬 토큰", required = true, example = "qwert")
    private String refreshToken;
}
