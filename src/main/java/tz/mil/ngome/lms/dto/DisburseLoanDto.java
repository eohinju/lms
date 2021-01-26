package tz.mil.ngome.lms.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.entity.Account;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DisburseLoanDto {
	
	private LocalDate date;
	private Account account;
	private LoanDto loan;
	
}
