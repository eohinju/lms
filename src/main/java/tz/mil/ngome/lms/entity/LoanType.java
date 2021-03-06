package tz.mil.ngome.lms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "loan_types")
public class LoanType extends BaseEntity {
	
	@Column(nullable = false, length = 16, unique = true)
	private String name;
	
	@Column(nullable = false)
	private double interest;
	
	@Column(nullable = false)
	private int min;
	
	@Column(nullable = false)
	private int max;
	
	@Column(nullable = false)
	private int periods;
	
	@Column(nullable = false)
	private Period period;

	public enum Period{
		DAY, WEEK, MONTH, YEAR
	}
}
