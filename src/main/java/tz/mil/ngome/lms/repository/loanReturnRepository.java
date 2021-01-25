package tz.mil.ngome.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tz.mil.ngome.lms.entity.LoanReturn;

public interface loanReturnRepository extends JpaRepository<LoanReturn, String> {

	List<LoanReturn> findByLoanAndMonth(String id, String month);

}
