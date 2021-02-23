package tz.mil.ngome.lms.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.TransactionDto;
import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface TransactionService {

	public Response<TransactionDto> journalLoan(Loan loan, Account payAccount, LocalDate date);
	public TransactionDto saveTransaction(TransactionDto transactionDto);
	Response<TransactionDto> journalReturn(Loan loan, Account payAccount, int amount, LocalDate date);

}
