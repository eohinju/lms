package tz.mil.ngome.lms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.repository.AccountTypeRepository;
import tz.mil.ngome.lms.utils.SpringContext;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
	
	@JsonIgnore
	private AccountTypeRepository accountTypeRepo = SpringContext.getBean(AccountTypeRepository.class);

	private String id;
	private String name;
	private AccountTypeDto type;
	
	public AccountDto(String id, String name, String typeId) {
		this.id = id;
		this.name = name;
		this.type = accountTypeRepo.findTypeById(typeId);
	}
}
