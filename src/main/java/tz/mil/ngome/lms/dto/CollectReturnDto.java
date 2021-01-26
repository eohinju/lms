package tz.mil.ngome.lms.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.Loan;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CollectReturnDto {

	private LocalDate date;
	private Account account;
	private Loan loan;
	private int amount;
	
}
