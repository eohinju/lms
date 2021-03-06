package tz.mil.ngome.lms.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.fasterxml.uuid.Logger;

import tz.mil.ngome.lms.dto.AccountDto;
import tz.mil.ngome.lms.dto.AccountTypeDto;
import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.exception.DuplicateDataException;
import tz.mil.ngome.lms.exception.InvalidDataException;
import tz.mil.ngome.lms.exception.NotFoundException;
import tz.mil.ngome.lms.repository.AccountRepository;
import tz.mil.ngome.lms.repository.AccountTypeRepository;
import tz.mil.ngome.lms.repository.MemberRepository;
import tz.mil.ngome.lms.utils.Response;
import tz.mil.ngome.lms.utils.ResponseCode;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	UserService userService;
	
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	AccountTypeRepository accountTypeRepo;
	
	@Autowired
	MemberRepository memberRepo;
	
	@Override
	public Response<AccountDto> createAccount(AccountDto accountDto) {
		Response<AccountDto> response = new Response<>();
		
		if(accountDto.getName()!=null && !accountDto.getName().isEmpty() && accountRepo.findByName(accountDto.getName()).size()>0)
			throw new DuplicateDataException("Name is already used by another account");
		
		if (accountDto.getType()==null || accountDto.getType().getId()==null || accountDto.getType().getId().isEmpty() || !accountTypeRepo.findById(accountDto.getType().getId()).isPresent()) 
			throw new InvalidDataException("Valid account type is required");

		if(accountDto.getName()==null || accountDto.getName().isEmpty()) 
			throw new InvalidDataException("Name is required");
		
		Account account = new Account();
		Logger.logInfo("Default code "+account.getCode());
		account.setName(accountDto.getName());
		account.setAccountType(accountTypeRepo.findById(accountDto.getType().getId()).get());
		account.setCreatedBy(userService.me().getId());
		Account savedAccount = accountRepo.save(account);
		response.setCode(ResponseCode.SUCCESS);
		response.setData(accountRepo.findAccountById(savedAccount.getId()));
		return response;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER')")
	@Override
	public Response<AccountDto> updateAccount(AccountDto accountDto) {
		Response<AccountDto> response = new Response<AccountDto>();
		/*
		 * Check if accountDto has id
		 * */
		if(accountDto.getId()==null || accountDto.getId().isEmpty()) 
			throw new InvalidDataException("No account found with given identity");

		Optional<Account> oAccount = accountRepo.findById(accountDto.getId());
		if(oAccount.isPresent()) {
			Account account = oAccount.get();
			if(account.getDeleted())
				throw new NotFoundException("No account found with given identity");
			
			if(accountDto.getName()!=null && accountRepo.findByName(accountDto.getName()).size()>0 && accountRepo.findByName(accountDto.getName()).get(0).getId()!=accountDto.getId())
				throw new DuplicateDataException("Name is already used by another account");
			
//			if (accountDto.getType()==null || !accountTypeRepo.findById(accountDto.getType().getId()).isPresent())
//				throw new InvalidDataException("Valid account type is required");
			
			account.setName(accountDto.getName());
//			account.setAccountType(accountTypeRepo.findById(accountDto.getType().getId()).get());
			Account savedAccount = accountRepo.save(account);
			response.setCode(ResponseCode.SUCCESS);
			response.setData(accountRepo.findAccountById(savedAccount.getId()));
			return response;
		}
		throw new NotFoundException("No account found with given identity");
		
	}

	@Override
	public Response<List<AccountDto>> getAccounts() {
		return new Response<List<AccountDto>>(ResponseCode.SUCCESS,"Success",accountRepo.findAllAccounts());
	}
	
	@Override
	public Response<List<AccountTypeDto>> getAccountTypes() {
		return new Response<List<AccountTypeDto>>(ResponseCode.SUCCESS,"Success",accountTypeRepo.findAllAccountTypes());
	}
	
	@Override
	public Response<String> deleteAccount(String id) {
		Response<String> response = new Response<>();
		if(id==null)
			throw new NotFoundException("No identity found");
		
		Optional<Account> oAccount = accountRepo.findById(id);
		if(oAccount.isPresent()) {
			Account account = oAccount.get();
			if(account.getDeleted())
				throw new NotFoundException("No account found with given identity");
			account.setDeleted(true);
			account.setDeletedBy("Deleter");
			account.setDeletedAt(LocalDateTime.now());
			accountRepo.save(account);
			response.setCode(ResponseCode.SUCCESS);
			response.setData(null);
			response.setMessage("Account deleted");
			return response;
		}else
			throw new NotFoundException("No account found with given identity");
	}

	@Override
	public Account getInterestAccount() {
		if(accountRepo.findById("2802e2767b8011eb90a635091736d7c9").isPresent())
			return accountRepo.findById("2802e2767b8011eb90a635091736d7c9").get();
		return null;
	}

	@Override
	public Account getBankAccount() {
		if(accountRepo.findById("1eb90a635091736d7c92802e2767b801").isPresent())
			return accountRepo.findById("1eb90a635091736d7c92802e2767b801").get();
		return null;
	}

	@Override
	public Response<String> createMemberAccount(MemberDto memberDto) {
		if(memberDto==null || memberDto.getId()==null || memberDto.getId().isEmpty() || !memberRepo.findById(memberDto.getId()).isPresent())
			throw new InvalidDataException("Valid member required");
		
		Member member = memberRepo.findById(memberDto.getId()).get();
		if(accountRepo.findByCode(member.getCompNumber())!=null)
			throw new DuplicateDataException("Member already has an account");
		Account account = new Account();
		account.setName(member.getName());
		account.setCode(member.getCompNumber());
		account.setAccountType(accountTypeRepo.findByName("Asset").get(0));
		account.setCreatedBy(userService.me().getId());
		accountRepo.save(account);
		return new Response<String>(ResponseCode.SUCCESS,"Success","Account created successfully");
	}

}
