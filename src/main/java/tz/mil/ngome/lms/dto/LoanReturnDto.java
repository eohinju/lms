package tz.mil.ngome.lms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.entity.LoanReturn.ReturnStatus;
import tz.mil.ngome.lms.repository.LoanRepository;
import tz.mil.ngome.lms.repository.MemberRepository;
import tz.mil.ngome.lms.utils.SpringContext;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanReturnDto {

	@JsonIgnore
	private LoanRepository loanRepo = SpringContext.getBean(LoanRepository.class);
	
	@JsonIgnore
	private MemberRepository memberRepo = SpringContext.getBean(MemberRepository.class);
	
	private String id;
	private LoanDto loan;
	private MemberDto member;
	private String name;
	private String month;
	private double amount;
	private ReturnStatus status;

	public LoanReturnDto(String id, String loanId, String memberId, String name, String month, double amount, ReturnStatus status) {
		this.id = id;
		this.name = name;
		this.month = month;
		this.amount = amount;
		this.status = status;
		this.loan = loanId==null?null:loanRepo.findLoanById(loanId);
		this.member = memberId==null?null:memberRepo.findMemberById(memberId);
	}
	
}
