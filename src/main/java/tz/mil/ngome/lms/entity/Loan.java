package tz.mil.ngome.lms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class Loan extends BaseEntity {
	
	@ManyToOne
	@JoinColumn(nullable = false,referencedColumnName = "id")
	private Member member;
	
	@ManyToOne
	@JoinColumn(nullable = false,referencedColumnName = "id")
	private LoanType loanType;
	
	@Column(nullable = false)
	private int amount;
	
	@Column(nullable = false)
	private int returns;
	
	private String unit;
	
	private String subUnit;
	
	private String loanName;
	
	private int interest;
	
	private int periods;
	
	private Period period;

}
