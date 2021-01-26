package tz.mil.ngome.lms.service;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.TransactionDto;
import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.Transaction;
import tz.mil.ngome.lms.entity.TransactionDetail;
import tz.mil.ngome.lms.repository.AccountRepository;
import tz.mil.ngome.lms.repository.TransactionDetailRepository;
import tz.mil.ngome.lms.repository.TransactionRepository;
import tz.mil.ngome.lms.utils.Response;
import tz.mil.ngome.lms.utils.ResponseCode;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	TransactionRepository transactionRepo;
	
	@Autowired
	TransactionDetailRepository detailRepo;
	
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	EntityManager entityManager;

	@Autowired
	UserService userService;
	
	@Override
	public Response<TransactionDto> journalLoan(Loan loan, Account payAccount, LocalDate date) {
		
		Transaction txn = new Transaction();
		txn.setCreatedBy(userService.me().getId());
		txn.setDate(date);
		txn.setCredit(loan.getAmount());
		txn.setDebit(loan.getAmount());
		txn.setDescription("Being loan to "+accountRepo.findByCode(loan.getMember().getCompNumber()).getName());
		transactionRepo.save(txn);
		TransactionDetail detail = new TransactionDetail(txn,payAccount,0,loan.getAmount());
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		detail = new TransactionDetail(txn,payAccount,0,loan.getAmount());
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		return new Response<TransactionDto>(ResponseCode.SUCCESS,"Success",transactionRepo.findTransactionById(txn.getId()));
	}


}
