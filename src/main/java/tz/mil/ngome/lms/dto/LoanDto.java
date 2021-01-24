package tz.mil.ngome.lms.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.repository.LoanTypeRepository;
import tz.mil.ngome.lms.repository.MemberRepository;
import tz.mil.ngome.lms.utils.SpringContext;
import tz.mil.ngome.lms.entity.Loan.LoanStatus;
import tz.mil.ngome.lms.entity.LoanType.Period;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanDto {

	@JsonIgnore
	private LoanTypeRepository loanTypeRepo = SpringContext.getBean(LoanTypeRepository.class);
	
	@JsonIgnore
	private MemberRepository memberRepo = SpringContext.getBean(MemberRepository.class);
	
	private String id;
	private MemberDto member;
	private LoanTypeDto loanType;
	private int amount;
	private LocalDate effectDate;
	private int returns;
	private int balance = 0;
	private String unit;
	private String subUnit;
	private String loanName;
	private double interest;
	private int periods;
	private Period period;
	private LoanStatus status;
	
	public LoanDto(String id, String memberId, String loanTypeId, int amount, LocalDate effectDate,
			int returns, String unit, String subUnit, String loanName, 
			double interest, int periods, Period period, LoanStatus status) {
		this.id = id;
		this.amount = amount;
		this.returns = returns;
		this.unit = unit;
		this.subUnit = subUnit;
		this.loanName = loanName;
		this.interest = interest;
		this.periods = periods;
		this.period = period;
		this.status = status;
		this.member = memberRepo.findMemberById(memberId);
		this.loanType = loanTypeRepo.findLoanTypeById(loanTypeId);
	}
}
