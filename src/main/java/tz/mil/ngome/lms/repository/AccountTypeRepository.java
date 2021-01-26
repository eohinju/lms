package tz.mil.ngome.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tz.mil.ngome.lms.dto.AccountTypeDto;
import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.entity.AccountType;

public interface AccountTypeRepository extends JpaRepository<AccountType, String> {

	List<AccountType> findByName(String name);

	@Query(value = "select * from accounttype where deleted=false order by name asc", nativeQuery = true)
	List<AccountType> getAll();

	@Query("SELECT new tz.mil.ngome.lms.dto.AccountTypeDto("
			+ "type.id, type.name)"
			+ "FROM AccountType AS type WHERE type.id=:id")
	AccountTypeDto findTypeById(String id);

	@Query("SELECT new tz.mil.ngome.lms.dto.AccountTypeDto("
			+ "type.id, type.name)"
			+ "FROM AccountType AS type order by type.name asc")
	List<AccountTypeDto> findAllAccountTypes();
}
