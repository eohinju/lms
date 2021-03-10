package tz.mil.ngome.lms.service;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.Member;

@Service
public interface ReturnService {

	void saveValidReturn(Loan loan, Account account, double amount, LocalDate date);
	void saveOverReturn(Member member, Account account, double amount, LocalDate date);
	void saveUnderReturn(Loan loan, Account account, double amount, LocalDate date);
	void saveInValidReturn(String rank,String name, Account account, double amount, LocalDate date);

	ResponseEntity<?> getReturnsReport(String month);
	ResponseEntity<?> getUnpaidLoans();
	
}
