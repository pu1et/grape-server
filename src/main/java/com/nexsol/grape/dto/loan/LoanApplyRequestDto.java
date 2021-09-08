package com.nexsol.grape.dto.loan;

import com.nexsol.grape.domain.Loan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class LoanApplyRequestDto {

    private Long id;
    private List<MultipartFile> images;
    private Long desiredLimit;
    private String applicationDate;

    public Loan toLoan(){

        return Loan.builder()
                .userId(id)
                .desiredLimit(desiredLimit)
                .applicationDate(applicationDate)
                .build();

    }
}
