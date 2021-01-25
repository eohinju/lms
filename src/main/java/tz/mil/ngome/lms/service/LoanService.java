package tz.mil.ngome.lms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.CollectReturnsDto;
import tz.mil.ngome.lms.dto.CollectedReturnsResponseDto;
import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface LoanService {

	Response<LoanDto> requestLoan(LoanDto loanDto);
	Response<LoanDto> registerLoan(LoanDto loanDto);
	Response<LoanDto> approveLoan(LoanDto loanDto);
	Response<LoanDto> authorizeLoan(LoanDto loanDto);
	Response<LoanDto> disburseLoan(LoanDto loanDto);
	Response<List<LoanDto>> getLoans();
	Response<List<LoanDto>> getRequestedLoans(String subUnit);
	Response<List<LoanDto>> getApprovedLoans();
	Response<List<LoanDto>> getAuthorizedLoans();
	Response<List<LoanDto>> getIncompleteDisbursedLoans();	
	Response<List<LoanDto>> getDisbursedLoans();	
	Response<CollectedReturnsResponseDto> collectLoansReturns(CollectReturnsDto returnDto);
	
}
