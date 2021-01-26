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

import tz.mil.ngome.lms.dto.AccountDto;
import tz.mil.ngome.lms.dto.AccountTypeDto;
import tz.mil.ngome.lms.service.AccountService;
import tz.mil.ngome.lms.utils.Response;

@CrossOrigin( origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "api/")
public class AccountController {

	@Autowired
	AccountService accountService;

	@PostMapping(value = "create-account", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
		return this.accountService.createAccount(accountDto);
	}

	@PostMapping(value = "update-account", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<AccountDto> updateAccount(@RequestBody AccountDto accountDto) {
		return this.accountService.updateAccount(accountDto);
	}

	@GetMapping(value = "get-accounts")
	private Response<List<AccountDto>> getAccounts() {
		return this.accountService.getAccounts();
	}
	
	@GetMapping(value = "get-account-types")
	private Response<List<AccountTypeDto>> getAccountTypes() {
		return this.accountService.getAccountTypes();
	}
	
	@PostMapping(value = "delete-account/{id}")
	private Response<String> deleteAccount(@PathVariable(name = "id") String id) {
		return this.accountService.deleteAccount(id);
	}

}
