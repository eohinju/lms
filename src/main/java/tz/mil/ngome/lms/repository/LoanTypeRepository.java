package tz.mil.ngome.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tz.mil.ngome.lms.entity.LoanType;

public interface LoanTypeRepository  extends JpaRepository<LoanType, String> {

}
