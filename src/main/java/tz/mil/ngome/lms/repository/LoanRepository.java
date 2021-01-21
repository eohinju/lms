package tz.mil.ngome.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.Loan.LoanStatus;

public interface LoanRepository  extends JpaRepository<Loan, String> {

	@Query("SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.deleted=false")
	List<LoanDto> getAllLoans();

	@Query("SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.subUnit=:subUnit and loan.status=:status")
	List<LoanDto> getLoansBySubUnitAndStatus(String subUnit, LoanStatus status);
	
	@Query("SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.status=:status")
	List<LoanDto> getLoansByStatus(LoanStatus status);
	
	@Query("SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.status=:status and loan.id not in (select loanReturn.loan from LoanReturn AS loanReturn)")
	List<LoanDto> getNonReturnedLoans(LoanStatus status);
	
	@Query("SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.id=:id")
	LoanDto findLoanById(String id);
	
}
