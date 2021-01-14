package tz.mil.ngome.lms.service;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.SignDto;
import tz.mil.ngome.lms.dto.SignedDto;
import tz.mil.ngome.lms.dto.UserDto;
import tz.mil.ngome.lms.entity.User;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface UserService {

	Response<String> signUp(UserDto userDto);
	Response<SignedDto> signIn(SignDto signDto);
	User me();
	String lang();
	
}
