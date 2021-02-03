package tz.mil.ngome.lms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.Loan.LoanStatus;

public interface LoanRepository  extends JpaRepository<Loan, String> {

	@Query(value = "SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount, loan.effectDate,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.deleted=false order by loan.createdAt desc",
			countQuery = " select count(loan) from Loan as loan where loan.deleted=false"
			)
	Page<LoanDto> getLoans(Pageable pageable);
	
	@Query(value = "SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount, loan.effectDate,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.deleted=false  and loan.subUnit=:subUnit order by loan.createdAt desc",
			countQuery = " select count(loan) from Loan as loan where loan.deleted=false and loan.subUnit=:subUnit"
			)
	Page<LoanDto> getSubUnitLoans(String subUnit, Pageable pageable);
	
	@Query(value = "SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount, loan.effectDate,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.deleted=false and loan.subUnit=:subUnit and loan.status=:status order by loan.createdAt desc",
			countQuery = " select count(loan) from Loan as loan where loan.deleted=false and loan.subUnit=:subUnit and loan.status=:status"
			)
	Page<LoanDto> getSubUnitLoansByStatus(String subUnit, int status, Pageable pageable);
	
	@Query("SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount, loan.effectDate,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.member.compNumber=:comp and loan.deleted=false order by loan.createdAt desc")
	List<LoanDto> getLoansByMember(int comp);

	@Query("SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount, loan.effectDate,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.subUnit=:subUnit and loan.status=:status order by loan.createdAt desc")
	List<LoanDto> getLoansBySubUnitAndStatus(String subUnit, LoanStatus status);
	
	@Query("SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount, loan.effectDate,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.status=:status order by loan.createdAt desc")
	List<LoanDto> getLoansByStatus(LoanStatus status);
	
	@Query("SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount, loan.effectDate,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.status=:status and loan.id not in (select loanReturn.loan from LoanReturn AS loanReturn) order by loan.createdAt desc")
	List<LoanDto> getNonReturnedLoans(LoanStatus status);
	
	@Query("SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount, loan.effectDate,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.id=:id order by loan.createdAt desc")
	LoanDto findLoanById(String id);

	@Query("SELECT new tz.mil.ngome.lms.dto.LoanDto("
			+ "loan.id, loan.member.id, loan.loanType.id, loan.amount, loan.effectDate,"
			+ "loan.returns, loan.unit, loan.subUnit, loan.loanName, loan.interest,"
			+ "loan.periods, loan.period, loan.status)"
			+ "FROM Loan AS loan WHERE loan.member.id=:id and loan.status=:status order by loan.createdAt desc")
	List<LoanDto> findLoansByMemberAndStatus(String id, LoanStatus status);

	@Query(value = "select * from loans where status=:status and id not in(select distinct loans as id from loanreturn where month=:month)", nativeQuery = true)
	List<Loan> getNonReturnedLoansOnMonth(String month, int status);

}
