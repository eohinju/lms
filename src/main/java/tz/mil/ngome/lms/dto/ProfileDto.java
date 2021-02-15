package tz.mil.ngome.lms.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfileDto {

	private String phone;
	private String subUnit;
	private String payAccount;
	private LocalDate dob;
	private LocalDate rod;
	
}
