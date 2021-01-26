package tz.mil.ngome.lms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.AccountDto;
import tz.mil.ngome.lms.dto.AccountTypeDto;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface AccountService {

	Response<AccountDto> createAccount(AccountDto accountDto);

	Response<AccountDto> updateAccount(AccountDto accountDto);

	Response<List<AccountDto>> getAccounts();
	
	Response<List<AccountTypeDto>> getAccountTypes();
	
	Response<String> deleteAccount(String id);
	
}
