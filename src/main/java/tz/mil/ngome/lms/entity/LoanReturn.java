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
	
	@Column(columnDefinition = "double default 0", nullable = false)
	private double amount;
	
	@Column(columnDefinition = "int default 0", nullable = false)
	private int diff;
	
	@Column(nullable = false, length = 7)
	private String month;

	@Column(nullable = true)
	private String name;

	@Column(columnDefinition = "int default 0")
	private int snr;

	@Column
	private ReturnStatus status = ReturnStatus.CORRECTLY_DEDUCTED;
	
	public enum ReturnStatus{
		CORRECTLY_DEDUCTED, NOT_DEDUCTED, WRONGLY_DEDUCTED, NON_MEMBER_DEDUCTION
	}

}
