package com.nexsol.grape.dto.member.request;

import com.nexsol.grape.domain.member.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupRequestDto {

    @NotBlank(message = "아임포트 uid는 필수입니다.")
    @Pattern(regexp = "^imp_", message = "아임포트 uid는 imp_로 시작되어야 합니다.")
    @ApiModelProperty(value = "아임포트 uid", required = true, example = "imp_499791764097")
    private String impUid; // 아임포트(본인인증) uid

    @NotBlank(message = "비밀번호는 필수입니다.")
    @ApiModelProperty(value = "비밀번호", required = true, example = "12345")
    private String password;

}
