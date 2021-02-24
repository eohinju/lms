package tz.mil.ngome.lms.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.LoanReturn;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.entity.Loan.LoanStatus;
import tz.mil.ngome.lms.repository.LoanRepository;
import tz.mil.ngome.lms.repository.LoanReturnRepository;

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
	
	@Override
	public void saveValidReturn(Loan loan, int amount, LocalDate date) {
		LoanReturn loanReturn = new LoanReturn();
		loanReturn.setAmount(amount);
		loanReturn.setLoan(loan);
		loanReturn.setMonth(loanService.month(date));
		loanReturn.setCreatedBy(userService.me().getId());
		loanReturnRepo.save(loanReturn);
		
		loan.setBalance(loan.getBalance()-amount);
		if(loan.getBalance()<=0)
			loan.setStatus(LoanStatus.COMPLETED);
		else
			loan.setStatus(LoanStatus.RETURNING);
		loanRepo.save(loan);
	}

	@Override
	public void saveOverReturn(Member member, int amount, LocalDate date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveUnderReturn(Member member, int amount, LocalDate date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveInValidReturn(int compNumber, int amount, LocalDate date) {
		// TODO Auto-generated method stub
		
	}

}
