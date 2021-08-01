package com.nexsol.grape.controller;

import com.nexsol.grape.response.ApiResponse;
import com.nexsol.grape.service.ImageService;
import com.nexsol.grape.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loan")
public class LoanController {

    private final LoanService loanService;
    private final ImageService imageService;

    /**
     * 대출 신청
     * @param applyLoanForm : userId, images, desiredLimit, applicationDate
     * @return ApiResponse
     */
    @PostMapping("")
    @ResponseBody
    public ApiResponse applyLoan(ApplyLoanForm applyLoanForm){

        loanService.applyLoan(applyLoanForm);
        return new ApiResponse(200, "업로드 성공", null);
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

        ApiResponse result = new ApiResponse(200, "이미지 파일 받기 성공", data);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/{loanId}/{userId}/test")
    public ApiResponse getImageFileLinks(@PathVariable("loanId") Long loanId, @PathVariable("userId") Long userId){

        List<String> imageNameList = imageService.getImageNameList(loanId, userId);

        return new ApiResponse(200, "이미지 파일 리스트", imageNameList);
    }
}
