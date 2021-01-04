package tz.mil.ngome.lms.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberDto {

	private String id;
	private String serviceNumber;
	private String firstName;
	private String middleName;
	private String lastName;
	private String phone;
	private String unit;
	private String subUnit;

}
