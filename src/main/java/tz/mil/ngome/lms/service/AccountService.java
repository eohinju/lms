package tz.mil.ngome.lms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.AccountDto;
import tz.mil.ngome.lms.dto.AccountTypeDto;
import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface AccountService {

	Response<AccountDto> createAccount(AccountDto accountDto);
	
	Response<String> createMemberAccount(MemberDto memberDto);

	Response<AccountDto> updateAccount(AccountDto accountDto);

	Response<List<AccountDto>> getAccounts();
	
	Response<List<AccountTypeDto>> getAccountTypes();
	
	Response<String> deleteAccount(String id);

	Account getInterestAccount();

	Account getBankAccount();

}
