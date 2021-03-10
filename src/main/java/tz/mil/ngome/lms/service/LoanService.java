package tz.mil.ngome.lms.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.*;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface LoanService {

	Response<LoanDto> requestLoan(LoanDto loanDto);
	Response<LoanDto> registerLoan(LoanDto loanDto);
	Response<LoanDto> approveLoan(LoanDto loanDto);
	Response<LoanDto> authorizeLoan(LoanDto loanDto);
	Response<String> cancelLoan(LoanDto loanDto);
	Response<LoanDto> denyLoan(LoanDenyDto loanDto);
	Response<LoanDto> updateLoan(LoanDto loanDto);
	Response<LoanDto> requestTopUpLoan(TopUpDto topUpDto);
	Response<LoanDto> registerTopUpLoan(TopUpDto topUpDto);
	Response<LoanDto> disburseLoan(DisburseLoanDto loanDto);
	Response<List<MappedStringListDto>> disburseLoans(DisburseLoansDto loansDto);
	Response<Page<LoanDto>> getLoans(Pageable pageable);
	Response<Page<LoanDto>> getLoans(int comp,Pageable pageable);
//	Response<List<LoanDto>> getLoans();
	Response<List<LoanDto>> getRequestedLoans(String subUnit);
	Response<List<LoanDto>> getApprovedLoans();
	Response<List<LoanDto>> getAuthorizedLoans();
	Response<List<LoanDto>> getIncompleteDisbursedLoans();	
	Response<List<LoanDto>> getDisbursedLoans();	
	Response<List<LoanReturnDto>> collectLoansReturns(CollectReturnsDto returnDto);
	Response<LoanDto> collectLoanReturn(CollectReturnDto returnDto);
	Response<List<String>> registerLoans(LoansDto loansDto);
	Response<LoanDto> joinLoans(LoanJoinRequestDto loanJoinRequestDto);
	
	ResponseEntity<?> getLoansReport(Loan.LoanStatus status);
	ResponseEntity<?> getLoansReport(Loan.LoanStatus status, String month);
	String month(LocalDate date);
}
