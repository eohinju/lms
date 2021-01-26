package tz.mil.ngome.lms.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.entity.Account;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DisburseLoansDto {
	
	private LocalDate date;
	private Account account;
	private List<LoanDto> loans;
	
}