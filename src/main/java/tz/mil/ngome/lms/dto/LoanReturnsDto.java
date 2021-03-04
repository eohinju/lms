package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.entity.LoanReturn;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoanReturnsDto {

	String month;
	double amount;
	LoanReturn.ReturnStatus status;
	
}
