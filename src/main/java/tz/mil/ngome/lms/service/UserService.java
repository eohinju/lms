package tz.mil.ngome.lms.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.RoleSettingDto;
import tz.mil.ngome.lms.dto.SignDto;
import tz.mil.ngome.lms.dto.SignedDto;
import tz.mil.ngome.lms.dto.SignupDto;
import tz.mil.ngome.lms.dto.UserDto;
import tz.mil.ngome.lms.entity.User;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface UserService {

	Response<String> signUp(SignupDto userDto);
	Response<SignedDto> signIn(SignDto signDto);
	User me();
	String lang();
	Response<Page<UserDto>> getUsers(Pageable pageable);
	Response<List<UserDto>> getSpecialUsers();
	Response<UserDto> setRole(RoleSettingDto user);

}
