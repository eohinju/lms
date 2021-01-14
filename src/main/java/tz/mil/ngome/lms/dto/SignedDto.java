package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignedDto {

	private String username;
	private String name;
	private String phone;
	private String role;
	private String token;
	
}
