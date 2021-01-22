package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

	private String id;
	private int compNumber;
	private String serviceNumber;
	private String rank;
	private String firstName;
	private String middleName;
	private String lastName;
	private String phone;
	private String unit;
	private String subUnit;

}
