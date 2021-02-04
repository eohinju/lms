package tz.mil.ngome.lms.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.CollectReturnDto;
import tz.mil.ngome.lms.dto.CollectReturnsDto;
import tz.mil.ngome.lms.dto.CollectedReturnsResponseDto;
import tz.mil.ngome.lms.dto.DisburseLoanDto;
import tz.mil.ngome.lms.dto.DisburseLoansDto;
import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.dto.MappedStringListDto;
import tz.mil.ngome.lms.dto.TopUpDto;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface LoanService {

	Response<LoanDto> requestLoan(LoanDto loanDto);
	Response<LoanDto> registerLoan(LoanDto loanDto);
	Response<LoanDto> approveLoan(LoanDto loanDto);
	Response<LoanDto> authorizeLoan(LoanDto loanDto);
	Response<String> cancelLoan(LoanDto loanDto);
	Response<LoanDto> denyLoan(LoanDto loanDto);
	Response<LoanDto> topUpLoan(TopUpDto topUpDto);
	Response<LoanDto> disburseLoan(DisburseLoanDto loanDto);
	Response<List<MappedStringListDto>> disburseLoans(DisburseLoansDto loansDto);
	Response<Page<LoanDto>> getLoans(Pageable pageable);
	Response<List<LoanDto>> getLoans(int comp);
	Response<List<LoanDto>> getLoans();
	Response<List<LoanDto>> getRequestedLoans(String subUnit);
	Response<List<LoanDto>> getApprovedLoans();
	Response<List<LoanDto>> getAuthorizedLoans();
	Response<List<LoanDto>> getIncompleteDisbursedLoans();	
	Response<List<LoanDto>> getDisbursedLoans();	
	Response<CollectedReturnsResponseDto> collectLoansReturns(CollectReturnsDto returnDto);
	Response<LoanDto> collectLoanReturn(CollectReturnDto returnDto);
	
}
