package com.nexsol.grape.controller;

import com.nexsol.grape.dto.loan.LoanApplyRequestDto;
import com.nexsol.grape.common.ApiResponseDto;
import com.nexsol.grape.service.ImageService;
import com.nexsol.grape.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class LoanController {

    private final LoanService loanService;
    private final ImageService imageService;

    /**
     * 대출 신청
     * @param loanApplyRequestDto : userId, images, desiredLimit, applicationDate
     * @return ApiResponse
     */
    @PostMapping("/api/loan")
    @ResponseBody
    public ApiResponseDto applyLoan(LoanApplyRequestDto loanApplyRequestDto){

        loanService.applyLoan(loanApplyRequestDto);
        return new ApiResponseDto(200, "업로드 성공", null);
    }

    @GetMapping(value = "/{loanId}/{userId}",
            produces = MediaType.MULTIPART_MIXED_VALUE
    )
    public ResponseEntity<?> getImageFiles(@PathVariable("loanId") Long loanId, @PathVariable("userId") Long userId){
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        List<ByteArrayResource> imageFiles = imageService.getImageFiles(loanId, userId);

        for(ByteArrayResource b: imageFiles){
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<ByteArrayResource> entity = new HttpEntity<>(b, headers);

            data.add("", entity);
        }

        ApiResponseDto result = new ApiResponseDto(200, "이미지 파일 받기 성공", data);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/{loanId}/{userId}/test")
    public ApiResponseDto getImageFileLinks(@PathVariable("loanId") Long loanId, @PathVariable("userId") Long userId){

        List<String> imageNameList = imageService.getImageNameList(loanId, userId);

        return new ApiResponseDto(200, "이미지 파일 리스트", imageNameList);
    }
}
