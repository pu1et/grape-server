package com.nexsol.grape.service;

import com.amazonaws.util.IOUtils;
import com.nexsol.grape.dto.loan.LoanApplyRequestDto;
import com.nexsol.grape.util.ObjectStorageUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
public class ImageServiceTest {

    static{
        System.setProperty("spring.config.location", "classpath:/application.yml,classpath:/ncp.yml,classpath:/test.yml");
    }

    @Autowired ImageService imageService;
    @Autowired ObjectStorageUtil objectStorageUtil;

    @Value("${custom.path.file}")
    String filepath;
    Long userId, loanId;

    @BeforeEach
    void beforeEach(){
        // 보험사 1개 추가
        objectStorageUtil.createFolder("1/");
        userId = 1L;
        loanId = 1L;
    }

    @AfterEach
    void afterEach(){
        // 지우는 순서는 상관없음 (파일, 폴더)
        List<String> objectKeyList = new ArrayList<>();
        objectKeyList.add("1/1/1.jpg");
        objectKeyList.add("1/1/2.jpg");
        objectKeyList.add("1/1/");
        objectKeyList.add("1/");

        objectStorageUtil.deleteImages(objectKeyList);
    }


    @Test
    @DisplayName("사용자가 이미지 리스트를 업로드하면 정상적으로 등록되어야 한다.")
    void uploadImages(){

        // give
        List<MultipartFile> images = new ArrayList<>();

        // when
        Integer originFolderSize = imageService.getFolderListSize(loanId);
        Integer originImageSize = imageService.getImageListSize(loanId, userId);

        // Multipartfile 리스트 생성
        try {
            File file = new File(filepath);
            FileItem fileItem = new DiskFileItem("testFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

            // 파일 2개 넣기
            images.add(multipartFile);
            images.add(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Object Storage에 이미지 파일 리스트 업로드
        LoanApplyRequestDto loanForm = new LoanApplyRequestDto(userId, images, 1000L, "2020-02-20");
        imageService.uploadImages(loanId, loanForm);

        Integer updateFolderSize = imageService.getFolderListSize(loanId);
        Integer updateImageSize = imageService.getImageListSize(loanId, userId);

        // then
        assertThat(updateFolderSize).isEqualTo(originFolderSize + 1);
        assertThat(updateImageSize).isEqualTo(originImageSize + 2);
    }

    @Test
    @DisplayName("사용자가 이미지 파일 리스트를 업로드하면 사용자 폴더가 한 개 생성되어야 한다.")
    void getFolderListSize(){
        // give
        List<MultipartFile> images = new ArrayList<>();

        // when
        // 이미지 파일 리스트를 업로드 하기 전 사용자 수
        Integer originFolderListSize = imageService.getFolderListSize(loanId);

        // Multipartfile 리스트 생성
        try {
            File file = new File(filepath);
            FileItem fileItem = new DiskFileItem("testFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

            // 파일을 이미지 파일 리스트에 넣기
            images.add(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Object Storage에 이미지 파일 리스트 업로드
        LoanApplyRequestDto loanForm = new LoanApplyRequestDto(userId, images, 1000L, "2020-02-20");
        imageService.uploadImages(loanId, loanForm);

        Integer imageListSize = imageService.getImageListSize(loanId,userId);

        // 이미지 파일 리스트를 업로드 한 후 사용자 수
        Integer updateFolderListSize = imageService.getFolderListSize(loanId);

        // then
        assertThat(updateFolderListSize).isEqualTo(originFolderListSize + 1);
    }

    @Test
    @DisplayName("사용자의 이미지 파일을 업로드한 만큼 사용자 폴더의 이미지 파일 개수가 증가해야 한다.")
    void getImageListSize(@Value("{custom.path.file}") String filePath){
        // give
        List<MultipartFile> images = new ArrayList<>();

        // when
        // 이미지 파일 리스트를 업로드 하기 전 이미지 파일 개수
        Integer originImageSize = imageService.getImageListSize(loanId, userId);

        // Multipartfile 리스트 생성
        try {
            File file = new File(filepath);
            FileItem fileItem = new DiskFileItem("testFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

            // 파일 2개를 이미지 파일 리스트에 넣기
            images.add(multipartFile);
            images.add(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Object Storage에 이미지 파일 리스트 업로드
        LoanApplyRequestDto loanForm = new LoanApplyRequestDto(userId, images, 1000L, "2020-02-20");
        imageService.uploadImages(loanId, loanForm);

        // 이미지 파일 리스트를 업로드 한 후 이미지 파일 개수
        Integer updateImageSize = imageService.getImageListSize(loanId, userId);

        // then
        assertThat(updateImageSize).isEqualTo(originImageSize + 2);
    }

    @Test
    @DisplayName("사용자가 업로드한 파일에 대해 파일명을 받을 수 있어야 한다.")
    void getImageNameList(){
        // give
        List<MultipartFile> images = new ArrayList<>();
        String originFileName = "1/1/1.jpg";

        // when
        File file = new File(filepath);

        // Multipartfile 리스트 생성
        try {
            FileItem fileItem = new DiskFileItem("testFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

            // 파일 넣기
            images.add(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Object Storage에 이미지 파일 리스트 업로드
        LoanApplyRequestDto loanForm = new LoanApplyRequestDto(userId, images, 1000L, "2020-02-20");
        imageService.uploadImages(loanId, loanForm);

        String uploadFileName = imageService.getImageNameList(loanId, userId).get(0);

        // then
        assertThat(uploadFileName).isEqualTo(originFileName);
    }

    @Test
    @DisplayName("사용자가 업로드한 파일에 대해 파일을 받을 수 있어야 한다.")
    void getImageFiles(){
        // give
        List<MultipartFile> images = new ArrayList<>();

        // when
        Integer originImageSize = imageService.getImageListSize(loanId, userId);

        File file = new File(filepath);
        long originFileSize = file.length();

        // Multipartfile 리스트 생성
        try {
            FileItem fileItem = new DiskFileItem("testFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

            // 파일 넣기
            images.add(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Object Storage에 이미지 파일 리스트 업로드
        LoanApplyRequestDto loanForm = new LoanApplyRequestDto(userId, images, 1000L, "2020-02-20");
        imageService.uploadImages(loanId, loanForm);

        // 이미지 파일 리스트를 업로드 한 후 이미지 파일 개수 및 파일 크기
        Integer updateImageSize = imageService.getImageListSize(loanId, userId);
        ByteArrayResource byteArrayResource = imageService.getImageFiles(loanId, userId).get(0);
        long uploadFileSize = byteArrayResource.contentLength();

        // then
        assertThat(updateImageSize).isEqualTo(originImageSize + 1);
        assertThat(uploadFileSize).isEqualTo(originFileSize);
    }

    @Test
    @DisplayName("이미지 리스트를 삭제 요청하면 폴더 및 내부 파일이 정상적으로 삭제되어야 한다.")
    void deleteImageFiles(){
        // give
        List<MultipartFile> images = new ArrayList<>();

        // when
        // Multipartfile 리스트 생성
        try {
            File file = new File(filepath);
            FileItem fileItem = new DiskFileItem("testFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

            // 파일을 이미지 파일 리스트에 넣기
            images.add(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Object Storage에 이미지 파일 리스트 업로드
        LoanApplyRequestDto loanForm = new LoanApplyRequestDto(userId, images, 1000L, "2020-02-20");
        imageService.uploadImages(loanId, loanForm);

        // 이미지 파일 리스트를 업로드 한 후 폴더와 파일 수
        Integer uploadFolderListSize = imageService.getFolderListSize(loanId);
        Integer uploadFileSize = imageService.getImageListSize(userId,loanId);

        // 이미지 파일 리스트 삭제
        imageService.deleteImageFiles(userId, loanId);

        // 이미지 파일 리스트를 삭제 한 후 폴더와 파일 수
        Integer deleteFolderListSize = imageService.getFolderListSize(loanId);
        Integer deleteFileSize = imageService.getImageListSize(userId,loanId);

        // then
        assertThat(deleteFolderListSize).isEqualTo(uploadFolderListSize - 1);
        assertThat(deleteFileSize).isEqualTo(uploadFileSize - 1);
    }
}
