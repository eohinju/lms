package tz.mil.ngome.lms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

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
public class LoanType extends BaseEntity {
	
	@Column(nullable = false, length = 16)
	private String nameOfLoan;
	
	@Column(nullable = false)
	private double interest;
	
	@Column(nullable = false)
	private int min;
	
	@Column(nullable = false)
	private int max;
	
	@Column(nullable = false)
	private int convertionPeriods;
	
	@Column(nullable = false)
	private Period period;

	public enum Period{
		DAY, WEEK, MONTH, YEAR
	}
}
