package  tz.mil.ngome.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tz.mil.ngome.lms.dto.AccountDto;
import tz.mil.ngome.lms.entity.Account;

public interface AccountRepository extends JpaRepository<Account, String> {

	List<Account> findByName(String name);

	@Query(value = "select * from accounts where deleted=false order by name asc", nativeQuery = true)
	List<Account> getAll();
	
	@Query("SELECT new tz.mil.ngome.lms.dto.AccountDto("
			+ "account.id, account.name, account.accountType.id)"
			+ "FROM Account AS account WHERE account.id=:id")
	AccountDto findAccountById(String id);
	
	@Query("SELECT new tz.mil.ngome.lms.dto.AccountDto("
			+ "account.id, account.name, account.accountType.id)"
			+ "FROM Account AS account WHERE account.name=:name")
	AccountDto findAccountByName(String name);

	@Query("SELECT new tz.mil.ngome.lms.dto.AccountDto("
			+ "account.id, account.name, account.accountType.id)"
			+ "FROM Account AS account where account.code=0 order by account.name asc")
	List<AccountDto> findAllAccounts();

	Account findByCode(int compNumber);

	@Query("SELECT new tz.mil.ngome.lms.dto.AccountDto("
			+ "account.id, account.name, account.accountType.id)"
			+ "FROM Account AS account WHERE account.code=:code")
	AccountDto findAccountByCode(int code);
		
}
