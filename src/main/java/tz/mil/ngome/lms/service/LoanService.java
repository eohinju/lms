package tz.mil.ngome.lms.service;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface LoanService {

	Response<LoanDto> requestLoan(LoanDto loanDto);
	Response<LoanDto> registerLoan(LoanDto loanDto);

}
