package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanTypeDto {

	private String id;
	private String name;
	private double interest;
	private int min;
	private int max;
	private int periods;
	private String period;
	
}
