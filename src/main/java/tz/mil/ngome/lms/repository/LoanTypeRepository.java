package tz.mil.ngome.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tz.mil.ngome.lms.dto.LoanTypeDto;
import tz.mil.ngome.lms.entity.LoanType;

public interface LoanTypeRepository  extends JpaRepository<LoanType, String> {

	@Query("SELECT new tz.mil.ngome.lms.dto.LoanTypeDto("
			+ "loanType.id,loanType.name,loanType.interest,loanType.min,loanType.max,"
			+ "loanType.periods,loanType.period)"
			+ "FROM LoanType AS loanType WHERE loanType.deleted=false")
	List<LoanTypeDto> getLoanTypes();

}
