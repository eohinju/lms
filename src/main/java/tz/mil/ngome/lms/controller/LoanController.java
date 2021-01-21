package tz.mil.ngome.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.exception.InvalidDataException;
import tz.mil.ngome.lms.service.LoanService;
import tz.mil.ngome.lms.utils.Response;

@CrossOrigin( origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "api/")
public class LoanController {

	@Autowired
	LoanService loanService;
	
	@GetMapping(value = "get-loans")
	private Response<List<LoanDto>> getLoans() {
		return this.loanService.getLoans();
	}
	
	@GetMapping(value = "get-requested-loans/{subUnit}")
	private Response<List<LoanDto>> getRequestedLoans(@PathVariable(name = "subUnit") String subUnit) {
		if(subUnit==null)
			throw new InvalidDataException("Sub-unit required");
		return this.loanService.getRequestedLoans(subUnit);
	}
	
	@GetMapping(value = "get-approved-loans")
	private Response<List<LoanDto>> getApprovedLoans() {
		return this.loanService.getApprovedLoans();
	}
	
	@GetMapping(value = "get-authorized-loans")
	private Response<List<LoanDto>> getAuthorizedLoans() {
		return this.loanService.getAuthorizedLoans();
	}
	
	@GetMapping(value = "get-disbursed-loans")
	private Response<List<LoanDto>> getDisbursedLoans() {
		return this.loanService.getDisbursedLoans();
	}
	
	@PostMapping(value = "register-loan", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> registerLoan(@RequestBody LoanDto loanDto) {
		return this.loanService.registerLoan(loanDto);
	}
	
	@PostMapping(value = "request-loan", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> requestLoan(@RequestBody LoanDto loanDto) {
		return this.loanService.requestLoan(loanDto);
	}
	
	@PostMapping(value = "approve-loan", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> approveLoan(@RequestBody LoanDto loanDto) {
		return this.loanService.approveLoan(loanDto);
	}
	
	@PostMapping(value = "authorize-loan", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> authorizeLoan(@RequestBody LoanDto loanDto) {
		return this.loanService.authorizeLoan(loanDto);
	}
	
	@PostMapping(value = "disburse-loan", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> disburseLoan(@RequestBody LoanDto loanDto) {
		return this.loanService.disburseLoan(loanDto);
	}
	
}
