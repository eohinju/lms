package tz.mil.ngome.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tz.mil.ngome.lms.dto.UserDto;
import tz.mil.ngome.lms.dto.ChangePasswordDto;
import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.dto.RoleSettingDto;
import tz.mil.ngome.lms.dto.SignDto;
import tz.mil.ngome.lms.dto.SignedDto;
import tz.mil.ngome.lms.dto.SignupDto;
import tz.mil.ngome.lms.service.UserService;
import tz.mil.ngome.lms.utils.Configuration;
import tz.mil.ngome.lms.utils.Response;

@CrossOrigin( origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "api/")
public class UserController {

	@Autowired
	UserService userService;
	
	Configuration conf = new Configuration();

	@PostMapping(value = "auth/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<String> signUp(@RequestBody SignupDto userDto) {
		return this.userService.signUp(userDto);
	}
	
	@PostMapping(value = "auth/signin", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<SignedDto> signIn(@RequestBody SignDto signDto) {
		return this.userService.signIn(signDto);
	}
	
	@GetMapping(value = "get-users")
	private Response<Page<UserDto>> getUsers(@RequestParam(name = "page", required = false) Integer page,@RequestParam(name = "size", required = false) Integer size) {
		if(page==null || size==null)
			return this.userService.getUsers(PageRequest.of(0, Integer.MAX_VALUE));
		int p = page.intValue()<0?0:page.intValue();
		int s = size.intValue()<0?conf.getDefaultPageSize():size.intValue();
		return this.userService.getUsers(PageRequest.of(p, s));
	}
	
	@GetMapping(value = "get-users/{data}")
	private Response<List<UserDto>> findMember(@PathVariable(name = "data", required = true) String data) {
		data = data==null?"":data.toLowerCase();
		return this.userService.findUsers(data);
	}
	
	@GetMapping(value = "get-special-users")
	private Response<List<UserDto>> getSpecialUsers() {
		return this.userService.getSpecialUsers();
	}
	
	@PostMapping(value = "set-role", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<UserDto> setRole(@RequestBody RoleSettingDto data) {
		return this.userService.setRole(data);
	}
	
	@PostMapping(value = "auth/change-password", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<String> changePassword(@RequestBody ChangePasswordDto passwordChange) {
		return this.userService.changePassword(passwordChange);
	}
	
}