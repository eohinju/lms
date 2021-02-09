package tz.mil.ngome.lms.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.entity.LoanType.Period;
import tz.mil.ngome.lms.utils.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loans")
public class Loan extends BaseEntity {
	
	@ManyToOne
	@JoinColumn(nullable = false,referencedColumnName = "id")
	private Member member;
	
	@ManyToOne
	@JoinColumn(nullable = false,referencedColumnName = "id")
	private LoanType loanType;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	private Loan clearer;
	
	@Column(nullable = false)
	private int amount;
	
	@Column(columnDefinition = "int default 0")
	private int amountToPay;
	
	@Column(nullable = true)
	private LocalDate effectDate;
	
	@Column(nullable = false)
	private int returns;
	
	private int balance = 0;
	
	private String unit;
	
	private String subUnit;
	
	private String loanName;
	
	private double interest;
	
	private int periods;
	
	private Period period;
	
	private LoanStatus status = LoanStatus.REQUESTED;
	
	private String remark;
	
	public enum LoanStatus{
		REQUESTED, APPROVED, AUTHORIZED, PAID, RETURNING, COMPLETED, CANCELED, DENIED
	}

}
