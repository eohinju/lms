package tz.mil.ngome.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tz.mil.ngome.lms.dto.CollectReturnDto;
import tz.mil.ngome.lms.dto.CollectReturnsDto;
import tz.mil.ngome.lms.dto.CollectedReturnsResponseDto;
import tz.mil.ngome.lms.dto.DisburseLoanDto;
import tz.mil.ngome.lms.dto.DisburseLoansDto;
import tz.mil.ngome.lms.dto.LoanDenyDto;
import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.dto.MappedStringListDto;
import tz.mil.ngome.lms.dto.TopUpDto;
import tz.mil.ngome.lms.exception.InvalidDataException;
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
	
//	@GetMapping(value = "get-loans")
//	private Response<List<LoanDto>> getLoans() {
//		return this.loanService.getLoans();
//	}
	@GetMapping(value = "get-loans")
	private Response<Page<LoanDto>> getLoans(@RequestParam(name = "page", required = false) Integer page,@RequestParam(name = "size", required = false) Integer size) {
		if(page==null || size==null)
			return this.loanService.getLoans(PageRequest.of(0, Integer.MAX_VALUE));
		int p = page.intValue()<0?0:page.intValue();
		int s = size.intValue()<0?conf.getDefaultPageSize():size.intValue();
		return this.loanService.getLoans(PageRequest.of(p, s));
	}
	
	@GetMapping(value = "get-loans/{comp}")
	private Response<Page<LoanDto>> getPersonsLoans(@PathVariable(name = "comp", required = false) Integer comp, @RequestParam(name = "page", required = false) Integer page,@RequestParam(name = "size", required = false) Integer size) {
		if(page==null || size==null) {
			page = 0;
			size = Integer.MAX_VALUE;
		}
		int p = page.intValue()<0?0:page.intValue();
		int s = size.intValue()<0?conf.getDefaultPageSize():size.intValue();
		if(comp!=null)
			return this.loanService.getLoans(comp,PageRequest.of(p, s));
		else
			return this.loanService.getLoans(PageRequest.of(p, s));
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
	private Response<LoanDto> disburseLoan(@RequestBody DisburseLoanDto loanDto) {
		return this.loanService.disburseLoan(loanDto);
	}
	
	@PostMapping(value = "disburse-loans", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<List<MappedStringListDto>> disburseLoans(@RequestBody DisburseLoansDto loansDto) {
		return this.loanService.disburseLoans(loansDto);
	}
	
	@PostMapping(value = "collect-returns")
	private Response<CollectedReturnsResponseDto> collectReturns(@ModelAttribute CollectReturnsDto collectReturnsDto) {
		return this.loanService.collectLoansReturns(collectReturnsDto);
	}
	
	@PostMapping(value = "collect-return", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> collectLoanReturn(@RequestBody CollectReturnDto returnDto) {
		return this.loanService.collectLoanReturn(returnDto);
	}
	
	@PostMapping(value = "cancel-loan", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<String> cancelLoan(@RequestBody LoanDto loanDto) {
		return this.loanService.cancelLoan(loanDto);
	}
	
	@PostMapping(value = "deny-loan", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> denyLoan(@RequestBody LoanDenyDto loanDto) {
		return this.loanService.denyLoan(loanDto);
	}

	@PostMapping(value = "update-loan", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> updateLoan(@RequestBody LoanDto loanDto) {
		return this.loanService.updateLoan(loanDto);
	}
	
	@PostMapping(value = "request-topup", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> requestTopUpLoan(@RequestBody TopUpDto topUpDto) {
		return this.loanService.requestTopUpLoan(topUpDto);
	}
	
	@PostMapping(value = "register-topup", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<LoanDto> registerTopUpLoan(@RequestBody TopUpDto topUpDto) {
		return this.loanService.registerTopUpLoan(topUpDto);
	}
	
}
