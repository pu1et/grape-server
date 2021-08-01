package com.nexsol.grape.service;

import com.amazonaws.util.IOUtils;
import com.nexsol.grape.controller.ApplyLoanForm;
import com.nexsol.grape.controller.UserForm;
import com.nexsol.grape.domain.Loan;
import com.nexsol.grape.domain.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class LoanServiceTest {

    static{
        System.setProperty("spring.config.location", "classpath:/application.yml,classpath:/ncp.yml,classpath:/test.yml");
    }

    @Autowired ImageService imageService;
    @Autowired LoanService loanService;
    @Autowired UserService userService;

    @Value("${custom.path.file}")
    String filepath;

    Long userId;

    @BeforeEach
    void beforeEach(){
        UserForm userForm = new UserForm("김이박", "010-0000-0000", "2000-12-31");
        User joinUser = userService.join(userForm);
        userId = joinUser.getId();
    }

    @Test
    @DisplayName("대출을 신청하면 loan 테이블과 Object Storage에 정상적으로 업로드 되어야 한다.")
    void applyLoan(){
        // give
        List<MultipartFile> images = new ArrayList<>();

        // when
        // Multipartfile 리스트 생성
        try {
            File file = new File(filepath);
            FileItem fileItem = new DiskFileItem("testFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

            // 파일 넣기
            images.add(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 대출 신청 폼 생성
        ApplyLoanForm loanForm = new ApplyLoanForm(userId, images, 1000L, "2020-02-20 20:20");
        // 대출 신청
        Loan loan = loanService.applyLoan(loanForm);

        Loan findLoan = loanService.findOne(loan.getId());
        Integer folderListSize = imageService.getFolderListSize(loan.getId());


        // then
        assertThat(loan).isEqualTo(findLoan);
        assertThat(folderListSize).isEqualTo(1);
    }

    @Test
    @DisplayName("정상적으로 대출 신청 정보가 저장되어야 한다.")
    void store(){
        // give
        Loan loan = new Loan(userId, 1000L, "2020-02-20 20:20");

        // when
        Loan storeLoan = loanService.store(loan);

        // then
        assertThat(loan).isEqualTo(storeLoan);
    }

    @Test
    @DisplayName("대출 신청 아이디를 입력하면 대출 정보가 반환되어야 한다.")
    void findOne(){
        // give
        Loan loan = new Loan(userId, 1000L, "2020-02-20 20:20");

        // when
        loanService.store(loan);
        Loan findLoan = loanService.findOne(loan.getId());

        // then
        assertThat(findLoan).isEqualTo(loan);
    }


}