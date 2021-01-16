package tz.mil.ngome.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tz.mil.ngome.lms.dto.LoanTypeDto;
import tz.mil.ngome.lms.service.LoanTypeService;
import tz.mil.ngome.lms.utils.Response;

@CrossOrigin( origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "api/")
public class LoanTypeController {

	@Autowired
	LoanTypeService loanTypeService;

	@PostMapping(value = "create-loan-type", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanTypeDto> registerLoanType(@RequestBody LoanTypeDto loanTypeDto) {
		return this.loanTypeService.createLoanType(loanTypeDto);
	}
	
	@GetMapping(value = "get-loan-types")
	private Response<List<LoanTypeDto>> getLoanTypes() {
		return this.loanTypeService.getLoanTypes();
	}
		
}
