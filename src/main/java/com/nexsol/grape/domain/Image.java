package com.nexsol.grape.domain;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    // objectName = loadId/userId
    private Long userId; // 사용자 아이디
    private Long loanId; // 대출 인덱스
    private MultipartFile file; // file.getBytes() 실제 내용
    private String name; // folder + name + 확장자

}
