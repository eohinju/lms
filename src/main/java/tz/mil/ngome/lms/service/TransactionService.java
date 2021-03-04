package tz.mil.ngome.lms.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.ExpenseDto;
import tz.mil.ngome.lms.dto.TransactionDto;
import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface TransactionService {

	Response<TransactionDto> journalLoan(Loan loan, Account payAccount, LocalDate date);
	TransactionDto saveTransaction(TransactionDto transactionDto);
	TransactionDto saveExpense(ExpenseDto expenseDto);
	Response<TransactionDto> journalReturn(Loan loan, Account account, double amount, LocalDate date);
	Response<TransactionDto> journalMember(Member member, Account account, double amount, LocalDate date);
	Response<TransactionDto> journalIncome(String member, Account account, double amount, LocalDate date);
	ResponseEntity<?> getTransactionsReport();
	Response<List<TransactionDto>> getTransactions(LocalDate start, LocalDate end);

}
