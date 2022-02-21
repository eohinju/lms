package tz.mil.ngome.lms.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.ExpenseDto;
import tz.mil.ngome.lms.dto.StatementDto;
import tz.mil.ngome.lms.dto.TransactionDetailDto;
import tz.mil.ngome.lms.dto.TransactionDto;
import tz.mil.ngome.lms.entity.*;
import tz.mil.ngome.lms.repository.AccountRepository;
import tz.mil.ngome.lms.repository.TransactionDetailRepository;
import tz.mil.ngome.lms.repository.TransactionRepository;
import tz.mil.ngome.lms.utils.*;

@Service
public class TransactionServiceImpl implements TransactionService {
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

	private final String logo = "classpath:reports/logo.png";

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
		logger.info("Journal loan");
		Transaction txn = new Transaction();
		txn.setCreatedBy(userService.me().getId());
		txn.setDate(date);
		txn.setCredit(loan.getBalance());
		txn.setDebit(loan.getBalance());
		double i = interest(loan.getBalance(),loan.getInterest());
		double a = loan.getBalance()-i;
		txn.setDescription("Mkopo kwa "+accountRepo.findByCode(loan.getMember().getCompNumber()).get(0).getName()+" ikiwa TZS "+ Formatter.toCash(a)+" ni mkopo na TZS "+Formatter.toCash(i)+" ni riba");
		transactionRepo.save(txn);
		TransactionDetail detail = new TransactionDetail(txn,payAccount,0,a);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		detail = new TransactionDetail(txn,accountService.getInterestAccount(),0,i);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		detail = new TransactionDetail(txn,accountRepo.findByCode(loan.getMember().getCompNumber()).get(0),loan.getBalance(),0);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		return new Response<TransactionDto>(ResponseCode.SUCCESS,"Success",transactionRepo.findTransactionById(txn.getId()));
	}
	
	@Override
	public Response<TransactionDto> journalReturn(Loan loan, Account account, double amount, LocalDate date) {
		logger.info("Journal return");
		Transaction txn = new Transaction();
		txn.setCreatedBy(userService.me().getId());
		txn.setDate(date);
		txn.setCredit(amount);
		txn.setDebit(amount);
		txn.setDescription("Rejesho la mkopo toka kwa "+accountRepo.findByCode(loan.getMember().getCompNumber()).get(0).getName());
		transactionRepo.save(txn);
		TransactionDetail detail = new TransactionDetail(txn,account,amount,0);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		detail = new TransactionDetail(txn,accountRepo.findByCode(loan.getMember().getCompNumber()).get(0),0,amount);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		return new Response<TransactionDto>(ResponseCode.SUCCESS,"Success",transactionRepo.findTransactionById(txn.getId()));
	}

	@Override
	public Response<TransactionDto> journalMember(Member member, Account account, double amount, LocalDate date) {
		logger.info("Journal member");
		Transaction txn = new Transaction();
		txn.setCreatedBy(userService.me().getId());
		txn.setDate(date);
		txn.setCredit(amount);
		txn.setDebit(amount);
		txn.setDescription("Fedha toka kwa "+accountRepo.findByCode(member.getCompNumber()).get(0).getName());
		transactionRepo.save(txn);
		TransactionDetail detail = new TransactionDetail(txn,account,amount,0);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		detail = new TransactionDetail(txn,accountRepo.findByCode(member.getCompNumber()).get(0),0,amount);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		return new Response<TransactionDto>(ResponseCode.SUCCESS,"Success",transactionRepo.findTransactionById(txn.getId()));
	}

	@Override
	public Response<TransactionDto> journalIncome(String member, Account account, double amount, LocalDate date) {
		logger.info("Journal income");
		Transaction txn = new Transaction();
		txn.setCreatedBy(userService.me().getId());
		txn.setDate(date);
		txn.setCredit(amount);
		txn.setDebit(amount);
		txn.setDescription("Being collection from "+member);
		transactionRepo.save(txn);
		TransactionDetail detail = new TransactionDetail(txn,account,amount,0);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		detail = new TransactionDetail(txn,accountRepo.findById(accountRepo.findAccountByName("Income").getId()).get(),0,amount);
		detail.setCreatedBy(userService.me().getId());
		detailRepo.save(detail);
		return new Response<TransactionDto>(ResponseCode.SUCCESS,"Success",transactionRepo.findTransactionById(txn.getId()));
	}

	@Override
	public ResponseEntity<?> getTransactionsReport() {
		logger.info("Get transaction report");
		Configuration conf = new Configuration();
		Map<String, Object> params = new HashMap<>();
		params.put("logo", logo);
		params.put("unit", conf.getUnit());
		params.put("fund", conf.getUnit()+" Relief Fund");
		params.put("title", "Miamala iliyofanyika Aug 2020");
		List<TransactionDto> transactionDtos = transactionRepo.getAllBetweenDates(LocalDate.parse("2020-09-01"),LocalDate.parse("2020-09-03"));
		if(transactionDtos.size()==0)
			transactionDtos.add(new TransactionDto());
		return Report.generate("transactions", transactionDtos , params);
	}

	@Override
	public ResponseEntity<?> getMemberTransactionsReport(int compNumber) {
		logger.info("Get member transaction report");
		Configuration conf = new Configuration();
		Map<String, Object> params = new HashMap<>();
		params.put("logo", logo);
		params.put("unit", conf.getUnit());
		params.put("fund", conf.getUnit()+" Relief Fund");
		params.put("title", "Taarifa ya Mwanachama");
		List<StatementDto> statementDtos = transactionRepo.getMemberStatement(compNumber);
		if(statementDtos.size()==0)
			statementDtos.add(new StatementDto());
		return Report.generate("statement", statementDtos , params);
	}

	@Override
	public Response<List<TransactionDto>> getTransactions(LocalDate start, LocalDate end) {
		logger.info("Get transactions");
		List<TransactionDto> transactionDtos = transactionRepo.getAllBetweenDates(start,end);
		return new Response<List<TransactionDto>>(ResponseCode.SUCCESS,"Success",transactionDtos);
	}

	@Override
	public TransactionDto saveTransaction(TransactionDto transactionDto) {
		logger.info("Save transaction");
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

	@Override
	public TransactionDto saveExpense(ExpenseDto expenseDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double interest(double total, double percent){
		return total - Math.round((100*total)/(100+percent));
	}


}
