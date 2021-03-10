package tz.mil.ngome.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tz.mil.ngome.lms.dto.DeductionReqestDto;
import tz.mil.ngome.lms.dto.LoanReturnDto;
import tz.mil.ngome.lms.dto.LoanReturnsDto;
import tz.mil.ngome.lms.dto.ReturnReportDataDto;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.LoanReturn;

public interface LoanReturnRepository extends JpaRepository<LoanReturn, String> {

	List<LoanReturn> findByLoanAndMonth(String id, String month);

	@Query("SELECT new tz.mil.ngome.lms.dto.LoanReturnsDto("
			+ "loanReturn.month, loanReturn.amount, loanReturn.status)"
			+ "FROM LoanReturn AS loanReturn WHERE loanReturn.loan.id=:id order by loanReturn.createdAt desc")
	List<LoanReturnsDto> findReturnsByLoan(String id);

	@Query("SELECT new tz.mil.ngome.lms.dto.LoanReturnsDto("
			+ "loanReturn.month, loanReturn.amount, loanReturn.status)"
			+ "FROM LoanReturn AS loanReturn WHERE loanReturn.loan.id=:id and loanReturn.status=:status order by loanReturn.createdAt desc")
	List<LoanReturnsDto> findReturnsByLoanAndStatus(String id, LoanReturn.ReturnStatus status);

	@Query("SELECT new tz.mil.ngome.lms.dto.LoanReturnDto("
			+ "loanReturn.id, loanReturn.loan.id, loanReturn.member.id, loanReturn.name, loanReturn.month, loanReturn.amount,loanReturn.status)"
			+ "FROM LoanReturn AS loanReturn WHERE loanReturn.month=:month order by loanReturn.createdAt asc")
	List<LoanReturnDto> findReturnsByMonth(String month);

	@Query("SELECT new tz.mil.ngome.lms.dto.ReturnReportDataDto("
			+ "loanReturn.status,loanReturn.name,loanReturn.amount)"
			+ "FROM LoanReturn AS loanReturn WHERE loanReturn.month=:month order by loanReturn.status asc, loanReturn.snr desc")
	List<ReturnReportDataDto> reportReturnsByMonth(String month);

	@Query("SELECT new tz.mil.ngome.lms.dto.DeductionReqestDto("
			+ "mbr.compNumber, mbr.serviceNumber, mbr.rank, concat(mbr.firstName,' ',mbr.middleName,' ',mbr.lastName), loan.balance, loan.returns, loan.joined, loan.status)"
			+ "FROM Loan loan JOIN loan.member mbr where loan.status=:paid")
	List<DeductionReqestDto> findUnpaidLoans(Loan.LoanStatus paid);
}
