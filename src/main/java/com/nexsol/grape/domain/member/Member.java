package com.nexsol.grape.domain.member;

import com.nexsol.grape.domain.member.enums.Authority;
import com.nexsol.grape.domain.member.enums.MemberStatus;
import com.nexsol.grape.dto.member.ImportMemberDto;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int gender;
    private Date birth;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String importKey;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    public static Member createMember(ImportMemberDto importMember, String password, PasswordEncoder passwordEncoder){
        return Member.builder()
                .name(importMember.getName())
                .gender(importMember.getGender().equals("male")? 0:1)
                .birth(Date.valueOf(importMember.getBirth()))
                .phone(importMember.getPhone())
                .importKey(importMember.getImportKey())
                .password(passwordEncoder.encode(password))
                .authority(Authority.ROLE_USER)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    // 참고 : setter 은 롬복으로 열어두면 원치 않는 값이 변경될 수도 있다.
    // 필요한 변수만 열어두자
    public void setStatus(MemberStatus status) {
        this.status = status;
    }
}
