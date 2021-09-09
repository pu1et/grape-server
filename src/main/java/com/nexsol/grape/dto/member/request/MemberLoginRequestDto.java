package com.nexsol.grape.dto.member.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequestDto {

    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    @Pattern(regexp = "^[0-9]*$", message = "휴대폰 번호는 숫자로만 구성이 되어있어야 합니다.")
    @ApiModelProperty(value = "휴대폰 번호", required = true, example = "01012345678")
    private String phone;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @ApiModelProperty(value = "비밀번호", required = true, example = "12345")
    private String password;

    public UsernamePasswordAuthenticationToken toAuthentication(){
        return new UsernamePasswordAuthenticationToken(phone, password);
    }
}
