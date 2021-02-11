package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.entity.Loan;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopUpDto {

	private LoanTypeDto loanType;
	private MemberDto member;
	private int amount;
	private String unit;
	private String subUnit;
	private Loan[] loans;
	
}
