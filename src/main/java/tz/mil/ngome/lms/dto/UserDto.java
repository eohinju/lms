package tz.mil.ngome.lms.dto;

import lombok.Getter;
import lombok.Setter;
import tz.mil.ngome.lms.entity.User.Role;

@Getter
@Setter
public class UserDto {
	private String id;
	private String username;
	private String password;
	private MemberDto member;
	private Role role;

}
