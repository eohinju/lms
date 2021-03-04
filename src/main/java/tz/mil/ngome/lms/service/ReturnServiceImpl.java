package tz.mil.ngome.lms.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.uuid.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.LoanDisburseDto;
import tz.mil.ngome.lms.dto.ReturnReportDataDto;
import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.LoanReturn;
import tz.mil.ngome.lms.entity.LoanReturn.ReturnStatus;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.entity.Loan.LoanStatus;
import tz.mil.ngome.lms.repository.LoanRepository;
import tz.mil.ngome.lms.repository.LoanReturnRepository;
import tz.mil.ngome.lms.utils.Configuration;
import tz.mil.ngome.lms.utils.Report;

@Service
public class ReturnServiceImpl implements ReturnService {

	@Autowired
	UserService userService;

	@Autowired
	LoanService loanService;
	
	@Autowired
	LoanReturnRepository loanReturnRepo;
	
	@Autowired
	LoanRepository loanRepo;

	@Autowired
	TransactionService transactionService;
	
	@Override
	public void saveValidReturn(Loan loan, Account account, double amount, LocalDate date) {
		LoanReturn loanReturn = new LoanReturn();
		loanReturn.setName(loan.getMember().getName());
		loanReturn.setAmount(amount);
		loanReturn.setMember(loan.getMember());
		loanReturn.setSnr(loan.getMember().getSnr());
		loanReturn.setLoan(loan);
		loanReturn.setStatus(ReturnStatus.CORRECTLY_DEDUCTED);
		loanReturn.setMonth(loanService.month(date));
		loanReturn.setCreatedBy(userService.me().getId());
		loanReturnRepo.save(loanReturn);
		
		loan.setBalance(loan.getBalance()-amount);
		if(loan.getBalance()<=0)
			loan.setStatus(LoanStatus.COMPLETED);
		else
			loan.setStatus(LoanStatus.RETURNING);
		loanRepo.save(loan);

		transactionService.journalReturn(loan,account,amount,date);
	}

	@Override
	public void saveOverReturn(Member member, Account account, double amount, LocalDate date) {
		LoanReturn loanReturn = new LoanReturn();
		loanReturn.setName(member.getName());
		loanReturn.setAmount(amount);
		loanReturn.setMember(member);
		loanReturn.setSnr(member.getSnr());
		loanReturn.setMonth(loanService.month(date));
		loanReturn.setStatus(ReturnStatus.WRONGLY_DEDUCTED);
		loanReturn.setCreatedBy(userService.me().getId());
		loanReturnRepo.save(loanReturn);

		transactionService.journalMember(member,account,amount,date);
	}

	@Override
	public void saveUnderReturn(Loan loan, Account account, double amount, LocalDate date) {
		LoanReturn loanReturn = new LoanReturn();
		loanReturn.setName(loan.getMember().getName());
		loanReturn.setLoan(loan);
		loanReturn.setAmount(amount);
		loanReturn.setMember(loan.getMember());
		loanReturn.setSnr(loan.getMember().getSnr());
		loanReturn.setMonth(loanService.month(date));
		loanReturn.setStatus(ReturnStatus.NOT_DEDUCTED);
		loanReturn.setCreatedBy(userService.me().getId());
		loanReturnRepo.save(loanReturn);

	}

	@Override
	public void saveInValidReturn(String rank, String name, Account account, double amount, LocalDate date) {
		Configuration conf = new Configuration();
		LoanReturn loanReturn = new LoanReturn();
		loanReturn.setSnr(conf.getRanks().indexOf(rank));
		loanReturn.setName(name);
		loanReturn.setAmount(amount);
		loanReturn.setMonth(loanService.month(date));
		loanReturn.setStatus(ReturnStatus.NON_MEMBER_DEDUCTION);
		loanReturn.setCreatedBy(userService.me().getId());
		loanReturnRepo.save(loanReturn);

		transactionService.journalIncome(name, account, amount, date);

	}

	@Override
	public ResponseEntity<?> getReturnsReport(String month) {
		Configuration conf = new Configuration();
		Map<String, Object> params = new HashMap<>();
		params.put("logo", Report.logo);
		params.put("unit", conf.getUnit());
		params.put("fund", conf.getUnit()+" Relief Fund");
		LocalDate start = LocalDate.parse(month+"-01");
		String mwezi=start.format(DateTimeFormatter.ofPattern("MMM yyyy"));
		params.put("title", "Marejesho ya mwezi "+mwezi);

		List<ReturnReportDataDto> returns = loanReturnRepo.reportReturnsByMonth(month);
		return Report.generate("returns", returns , params);
	}

}
