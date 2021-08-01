package com.nexsol.grape.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Entity
@Getter @Setter
@AllArgsConstructor
public class Loan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long desiredLimit;
    private Date applicationDate;

    @Builder
    public Loan(Long userId, Long desiredLimit, String applicationDate){
        this.userId = userId;
        this.desiredLimit = desiredLimit;
        this.applicationDate = stringToDateTime(applicationDate);
    }


    private Date stringToDateTime(String applicationDate){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            java.util.Date utilDate = dateFormat.parse(applicationDate);
            return new Date(utilDate.getTime());

        }catch(ParseException p){
            throw new IllegalStateException("유효한 신청 일시가 입력되지 않았습니다.");
        }
    }
}
