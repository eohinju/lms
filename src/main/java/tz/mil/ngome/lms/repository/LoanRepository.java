package tz.mil.ngome.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tz.mil.ngome.lms.entity.Loan;

public interface LoanRepository  extends JpaRepository<Loan, String> {

}
