package tz.mil.ngome.lms.utils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.AccountType;
import tz.mil.ngome.lms.entity.User;
import tz.mil.ngome.lms.entity.User.Role;
import tz.mil.ngome.lms.repository.AccountRepository;
import tz.mil.ngome.lms.repository.AccountTypeRepository;
import tz.mil.ngome.lms.repository.UserRepository;

@Component
public class Initializer {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	AccountTypeRepository accountTypeRepo;
	
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
    PasswordEncoder encoder;
	
	@PostConstruct
	public void init() {
		initializeUser();
		initializeAccountTypes();
		initializeAccounts();
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
	
	private void initializeAccounts() {
		if(accountRepo.count()>0)
			return;
		Account account = new Account();
		account.setName("Interest");
		account.setAccountType(accountTypeRepo.findByName("Revenue").get(0));
		account.setCreatedBy("Initializer");
		try{accountRepo.save(account);}catch(Exception e) {}
	}

}
