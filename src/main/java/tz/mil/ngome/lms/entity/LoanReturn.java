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
public class LoanReturn extends BaseEntity {
	
	@Column(nullable = false, length = 16)
	private String loan;
	
	@Column(nullable = false, length = 20)
	private int amount;
	
	@Column(nullable = false, length = 16)
	private String month;

}
