package tz.mil.ngome.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tz.mil.ngome.lms.dto.LoanReturnsDto;
import tz.mil.ngome.lms.entity.LoanReturn;

public interface LoanReturnRepository extends JpaRepository<LoanReturn, String> {

	List<LoanReturn> findByLoanAndMonth(String id, String month);

	@Query("SELECT new tz.mil.ngome.lms.dto.LoanReturnsDto("
			+ "loanReturn.month, loanReturn.amount)"
			+ "FROM LoanReturn AS loanReturn WHERE loanReturn.loan.id=:id order by loanReturn.createdAt desc")
	List<LoanReturnsDto> findReturnsByLoan(String id);

}
