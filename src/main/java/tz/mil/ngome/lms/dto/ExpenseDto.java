package tz.mil.ngome.lms.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {

	private LocalDate date;
	private String description;
	private AccountDto account;
	private AccountDto expense;
	private int amount;
//	private List<>
	
}
