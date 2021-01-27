package tz.mil.ngome.lms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.repository.AccountTypeRepository;
import tz.mil.ngome.lms.repository.TransactionDetailRepository;
import tz.mil.ngome.lms.utils.SpringContext;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
	
	@JsonIgnore
	private AccountTypeRepository accountTypeRepo = SpringContext.getBean(AccountTypeRepository.class);

	@JsonIgnore
	private TransactionDetailRepository detailsRepo = SpringContext.getBean(TransactionDetailRepository.class);
	
	private String id;
	private String name;
	private AccountTypeDto type;
	private int debit = 0;
	private int credit = 0;

	public AccountDto(String id, String name, String typeId) {
		name.replace("  ", " ");
		this.id = id;
		this.name = name;
		this.type = accountTypeRepo.findTypeById(typeId);
		this.debit = detailsRepo.getDebitByAccount(id)!=null?detailsRepo.getDebitByAccount(id):0;
		this.credit = detailsRepo.getCreditByAccount(id)!=null?detailsRepo.getCreditByAccount(id):0;
		this.debit = this.debit>this.credit?this.debit-this.credit:0;
		this.credit = this.credit>this.debit?this.credit-this.debit:0;
	}
	
}
