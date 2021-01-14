package tz.mil.ngome.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tz.mil.ngome.lms.dto.SignDto;
import tz.mil.ngome.lms.dto.SignedDto;
import tz.mil.ngome.lms.dto.UserDto;
import tz.mil.ngome.lms.service.UserService;
import tz.mil.ngome.lms.utils.Response;

@CrossOrigin( origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "api/")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping(value = "auth/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<String> signUp(@RequestBody UserDto userDto) {
		return this.userService.signUp(userDto);
	}
	
	@PostMapping(value = "auth/signin", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<SignedDto> signIn(@RequestBody SignDto signDto) {
		return this.userService.signIn(signDto);
	}
	
}