package tz.mil.ngome.lms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	
	@Column(nullable = false, length = 16)
	private int amount;
	
	@Column(nullable = false, length = 16)
	private int returns;

}
