package tz.mil.ngome.lms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.utils.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan_returns")
public class LoanReturn extends BaseEntity {
	
	@ManyToOne
	@JoinColumn(name = "loan",nullable = true,referencedColumnName = "id")
	private Loan loan;
	
	@ManyToOne
	@JoinColumn(name = "member",nullable = true,referencedColumnName = "id")
	private Member member;
	
	@Column(columnDefinition = "int default 0", nullable = false)
	private int amount;
	
	@Column(columnDefinition = "int default 0", nullable = false)
	private int diff;
	
	@Column(nullable = false, length = 7)
	private String month;

	@Column
	private ReturnStatus status = ReturnStatus.CORRECT_DEDUCTION;
	
	public enum ReturnStatus{
		UNDER_DEDUCTION, CORRECT_DEDUCTION, OVER_DEDUCTION
	}
}
