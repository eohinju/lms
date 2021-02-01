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
	@JoinColumn(name = "loan",nullable = false,referencedColumnName = "id")
	private Loan loan;
	
	@Column(nullable = false)
	private int amount;
	
	@Column(nullable = false, length = 7)
	private String month;

}
