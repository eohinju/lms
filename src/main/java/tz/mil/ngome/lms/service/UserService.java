package tz.mil.ngome.lms.service;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.UserDto;
import tz.mil.ngome.lms.entity.User;

@Service
public interface UserService {

	public User registerUser(UserDto userDto);
	
}
