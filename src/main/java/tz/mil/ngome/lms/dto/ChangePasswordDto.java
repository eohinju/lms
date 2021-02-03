package tz.mil.ngome.lms.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordDto {

	private String username;
	private String oldPassword;
	private String newPassword;
	
}
