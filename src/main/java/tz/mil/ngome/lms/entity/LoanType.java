package tz.mil.ngome.lms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LoanType extends BaseEntity {
	
	@Column(nullable = false, length = 16)
	private String nameOfLoan;
	
	@Column(nullable = false, length = 5)
	private int min;
	
	@Column(nullable = false, length = 5)
	private int max;
	
	@Column(nullable = false, length = 5)
	private int convertionPeriods;
	
	@Column(nullable = false, length = 5)
	private int periodSize;

}
