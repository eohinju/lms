package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tz.mil.ngome.lms.entity.User;
import tz.mil.ngome.lms.entity.User.Role;

@Setter
@Getter
@AllArgsConstructor
public class RoleSettingDto {

	private User user;
	private Role role;
	private String subUnit;
	
}
