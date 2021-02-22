package tz.mil.ngome.lms.service;

import java.time.LocalDate;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.TransactionDetailDto;
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
		detail = new TransactionDetail(txn,accountRepo.findByCode(loan.getMember().getCompNumber()),loan.getAmount(),0);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		return new Response<TransactionDto>(ResponseCode.SUCCESS,"Success",transactionRepo.findTransactionById(txn.getId()));
	}
	
//	@Override
	public Response<TransactionDto> journalReturn(Loan loan, Account payAccount, int amount, LocalDate date) {		
		Transaction txn = new Transaction();
		txn.setCreatedBy(userService.me().getId());
		txn.setDate(date);
		txn.setCredit(amount);
		txn.setDebit(amount);
		txn.setDescription("Being loan return from "+accountRepo.findByCode(loan.getMember().getCompNumber()).getName());
		transactionRepo.save(txn);
		TransactionDetail detail = new TransactionDetail(txn,payAccount,amount,0);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		int i = (int) Math.floor(amount*(loan.getInterest()/100));
		detail = new TransactionDetail(txn,accountRepo.findByCode(loan.getMember().getCompNumber()),0,amount-i);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		detail = new TransactionDetail(txn,accountRepo.findById(accountRepo.findAccountByName("Interest").getId()).get(),0,i);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		return new Response<TransactionDto>(ResponseCode.SUCCESS,"Success",transactionRepo.findTransactionById(txn.getId()));
	}

	@Override
	public TransactionDto saveTransaction(TransactionDto transactionDto) {
		Transaction txn = new Transaction();
		txn.setCreatedBy(userService.me().getId());
		txn.setDate(transactionDto.getDate());
		txn.setDescription(transactionDto.getDescription());
		transactionRepo.save(txn);
		int debit = 0;
		int credit = 0;
		for(TransactionDetailDto detail: transactionDto.getDetails() ) {
			TransactionDetail txnDetail = new TransactionDetail(txn,accountRepo.findById(detail.getAccount().getId()).get(),detail.getDebit(),detail.getCredit());
			txnDetail.setCreatedBy(userService.me().getId());
			detailRepo.save(txnDetail);
			debit+=detail.getDebit();
			credit+=detail.getCredit();
		}
		txn.setCredit(credit);
		txn.setDebit(debit);
		if(credit==debit)
			transactionRepo.save(txn);
		else
			transactionRepo.delete(txn);
		return transactionRepo.findTransactionById(txn.getId());
	}


}
