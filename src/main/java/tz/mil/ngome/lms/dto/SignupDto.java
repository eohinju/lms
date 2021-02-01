package tz.mil.ngome.lms.dto;

import lombok.Getter;
import lombok.Setter;
import tz.mil.ngome.lms.entity.User.Role;

@Setter
@Getter
public class SignupDto {

	private String id;
	private String username;
	private String password;
	private MemberDto member;
	private Role role;
	
}
