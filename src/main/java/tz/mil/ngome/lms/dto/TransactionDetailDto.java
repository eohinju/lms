package tz.mil.ngome.lms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.repository.AccountRepository;
import tz.mil.ngome.lms.utils.SpringContext;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailDto {

	@JsonIgnore
	private AccountRepository accountRepo = SpringContext.getBean(AccountRepository.class);
	
	private String id;
	private AccountDto account;
	private double debit;
	private double credit;
	
	public TransactionDetailDto(String id, String accountId, double debit, double credit) {
		this.id = id;
		this.debit = debit;
		this.credit = credit;
		this.account = accountRepo.findAccountById(accountId);
	}
}
