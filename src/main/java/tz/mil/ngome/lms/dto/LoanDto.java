package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.entity.LoanType;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.entity.Loan.LoanStatus;
import tz.mil.ngome.lms.entity.LoanType.Period;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanDto {

	private Member member;
	private LoanType loanType;
	private int amount;
	private int returns;
	private String unit;
	private String subUnit;
	private String loanName;
	private double interest;
	private int periods;
	private Period period;
	private LoanStatus status;
	
}
