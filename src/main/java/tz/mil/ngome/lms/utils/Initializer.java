package tz.mil.ngome.lms.utils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.AccountType;
import tz.mil.ngome.lms.entity.LoanType;
import tz.mil.ngome.lms.entity.User;
import tz.mil.ngome.lms.entity.User.Role;
import tz.mil.ngome.lms.repository.AccountRepository;
import tz.mil.ngome.lms.repository.AccountTypeRepository;
import tz.mil.ngome.lms.repository.LoanTypeRepository;
import tz.mil.ngome.lms.repository.UserRepository;

@Component
public class Initializer {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	AccountTypeRepository accountTypeRepo;

	@Autowired
	LoanTypeRepository loanTypeRepository;
	
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
    PasswordEncoder encoder;
	
	@PostConstruct
	public void init() {
		initializeUser();
		initializeAccountTypes();
		initializeAccounts();
		initializeLoanTypes();
	}
	
	private void initializeUser() {
		if(userRepo.count()>0)
			return;
		User user = new User("root","root@mail.com",encoder.encode("toor"),Role.ROLE_ADMIN,false,null);
		user.setCreatedBy("Initializer");
		try {userRepo.save(user);}catch(Exception e) {}
	}
	
	private void initializeAccountTypes() {
		if(accountTypeRepo.count()>0)
			return;
		String[] types = {"Asset","Liability","Capital","Revenue","Expense"};
		for(String type : types) {
			AccountType aType = new AccountType(type);
			aType.setCreatedBy("Initializer");
			try { accountTypeRepo.save(aType); }catch(Exception e) {}
		}
	}

	private void initializeLoanTypes() {
		if(loanTypeRepository.count()>0)
			return;
		LoanType type = new LoanType();
		type.setCreatedBy("Initializer");
		type.setName("Maendeleo");
		type.setMin(0);
		type.setMax(8000000);
		type.setInterest(10);
		type.setPeriods(6);
		type.setPeriod(LoanType.Period.MONTH);
		loanTypeRepository.save(type);
	}
	
	private void initializeAccounts() {
//		if(accountRepo.count()>0)
//			return;
		Account account = new Account();
		account.setName("Interest");
		account.setId("2802e2767b8011eb90a635091736d7c9");
		account.setAccountType(accountTypeRepo.findByName("Revenue").get(0));
		account.setCreatedBy("Initializer");
		try{accountRepo.save(account);}catch(Exception e) {}

		account = new Account();
		account.setName("Bank");
		account.setId("1eb90a635091736d7c92802e2767b801");
		account.setAccountType(accountTypeRepo.findByName("Asset").get(0));
		account.setCreatedBy("Initializer");
		try{accountRepo.save(account);}catch(Exception e) {}

		account = new Account();
		account.setName("Income");
		account.setId("635091736d7c9281eb90a02e2767b801");
		account.setAccountType(accountTypeRepo.findByName("Revenue").get(0));
		account.setCreatedBy("Initializer");
		try{accountRepo.save(account);}catch(Exception e) {}
	}

}
