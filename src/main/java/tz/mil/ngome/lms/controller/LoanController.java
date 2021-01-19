package tz.mil.ngome.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.service.LoanService;
import tz.mil.ngome.lms.utils.Configuration;
import tz.mil.ngome.lms.utils.Response;

@CrossOrigin( origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "api/")
public class LoanController {

	@Autowired
	LoanService loanService;
	
	Configuration conf = new Configuration();

	@PostMapping(value = "register-loan", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> registerLoan(@RequestBody LoanDto loanDto) {
		return this.loanService.registerLoan(loanDto);
	}
	
	@PostMapping(value = "request-loan", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> requestLoan(@RequestBody LoanDto loanDto) {
		return this.loanService.requestLoan(loanDto);
	}
	
}
