package com.nexsol.grape.service;

import com.nexsol.grape.dto.loan.LoanApplyRequestDto;
import com.nexsol.grape.domain.Loan;
import com.nexsol.grape.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LoanService {

    private final ImageService imageService;
    private final MemberService memberService;
    private final AuthService authService;
    private final LoanRepository loanRepository;

    /**
     * 대출 신청
     * @param loanForm : URL 요청시 받은 대출 신청 폼
     * @return 저장된 loan 테이블의 인덱스
     */
    @Transactional
    public Loan applyLoan(LoanApplyRequestDto loanForm){

        Loan loan = loanForm.toLoan();

        // loan 테이블 저장
        Long loanId = store(loan).getId();

        // Object Storage 에 이미지 파일 업로드
        imageService.uploadImages(loanId, loanForm);

        return loan;
    }

    public Loan store(Loan loan){
        Loan save = loanRepository.save(loan);
        return save;
    }


    public Loan findOne(Long loanId){
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalStateException("해당 대출 이력이 존재하지 않습니다."));
    }
}
