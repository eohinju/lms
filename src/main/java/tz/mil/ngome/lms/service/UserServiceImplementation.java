package tz.mil.ngome.lms.service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.ChangePasswordDto;
import tz.mil.ngome.lms.dto.RoleSettingDto;
import tz.mil.ngome.lms.dto.SignDto;
import tz.mil.ngome.lms.dto.SignedDto;
import tz.mil.ngome.lms.dto.SignupDto;
import tz.mil.ngome.lms.dto.UserDto;
import tz.mil.ngome.lms.entity.User;
import tz.mil.ngome.lms.entity.User.Role;
import tz.mil.ngome.lms.exception.DuplicateDataException;
import tz.mil.ngome.lms.exception.InvalidDataException;
import tz.mil.ngome.lms.exception.UnauthorizedException;
import tz.mil.ngome.lms.repository.MemberRepository;
import tz.mil.ngome.lms.repository.UserRepository;
import tz.mil.ngome.lms.security.JwtProvider;
import tz.mil.ngome.lms.utils.EmailSender;
import tz.mil.ngome.lms.utils.Response;
import tz.mil.ngome.lms.utils.ResponseCode;

@Service
public class UserServiceImplementation implements UserService {

	@Autowired
    AuthenticationManager authenticationManager;
	
	@Autowired
    JwtProvider jwtProvider;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	MemberRepository memberRepo;
	
	@Autowired
    PasswordEncoder encoder;
	
	@Autowired
	EmailSender emailSender;
	
	@Override
	public Response<String> signUp(SignupDto userDto) {
		Response<String> response = new Response<String>();
		
		if(userDto.getUsername()==null || userDto.getUsername().isEmpty())
			throw new InvalidDataException("Username is required");
			
		if(userRepo.findByUsername(userDto.getUsername()).isPresent())
			throw new DuplicateDataException("Username is already used");
		
		if(userDto.getMember()==null)
			throw new InvalidDataException("Member is required");
		
		if(userDto.getMember().getId()==null || !memberRepo.findById(userDto.getMember().getId()).isPresent())
			throw new InvalidDataException("Invalid member provided");
		
		if(userRepo.findByMemberId(userDto.getMember().getId()).isPresent())
			throw new DuplicateDataException("Member already has an account");
		
		User user = new User();
		BeanUtils.copyProperties(userDto, user, "id");
		user.setMember(memberRepo.findById(userDto.getMember().getId()).get());
		user.setPassword(encoder.encode(userDto.getPassword()==null?"":userDto.getPassword()));
		user.setRole(Role.ROLE_MEMBER);
		user.setCreatedBy("UserService");
		User savedUser = userRepo.save(user);
		if(savedUser!=null) {
			response.setCode(ResponseCode.SUCCESS);
			response.setData("Signed up successfully");
		}else {
			response.setCode(ResponseCode.FAILURE);
			response.setData("Sorry, could not sign up. Please start again");
		}
		return response;
	}

	@Override
	public Response<SignedDto> signIn(SignDto signDto) {
		Response<SignedDto> response = new Response<SignedDto>();
		
		if(signDto.getUsername()==null || signDto.getUsername().isEmpty())
			throw new InvalidDataException("Username is required");
		
		Authentication authentication = authenticationManager.authenticate(
        		new UsernamePasswordAuthenticationToken(
        				signDto.getUsername(),
        				signDto.getPassword()
        		)
			);        
		SecurityContextHolder.getContext().setAuthentication(authentication);

		
		
		Optional<User> oUser = userRepo.findByUsername(authentication.getName());
		if(oUser.isPresent()) {
			User user = oUser.get();
			String jwt = "";
			if(!user.isChangePassword())
				jwt = jwtProvider.generateJwtToken(authentication);
			else
				response.setMessage("Change password");
			SignedDto signed = new SignedDto();
			if(user.getMember()!=null) {
				signed.setPhone(user.getMember().getPhone());
				signed.setName(user.getMember().getShortName());
				signed.setSvc(user.getMember().getServiceNumber());
			}else {
				
			}
			signed.setRole(user.getRole().name());
			signed.setUsername(user.getUsername());
			signed.setToken(jwt);
			response.setCode(ResponseCode.SUCCESS);
			response.setData(signed);
		}else {
			response.setCode(ResponseCode.FAILURE);
			response.setMessage("Not authorized");
		}
		return response;
	}

	@Override
	public User me() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication==null)
			return null;
		Optional<User> oUser = userRepo.findByUsername(authentication.getName());
		if(oUser.isPresent())
			return oUser.get();
		return null;
	}

	@Override
	public String lang() {
		// TODO Auto-generated method stub
		return "sw";
	}

	@Override
	public Response<Page<UserDto>> getUsers(Pageable pageable) {
		return new Response<Page<UserDto>> (ResponseCode.SUCCESS,"Success",userRepo.getUsers(pageable));
	}
	
	@Override
	public Response<List<UserDto>> findUsers(String data) {
		String[] parts = data.trim().split(" ");
		Set<UserDto> users = new HashSet<UserDto>();
		for(String part:parts) {
			List<UserDto> listMembers = userRepo.findUsers(part);
			if(listMembers!=null && !listMembers.isEmpty()) {
				for(UserDto m:listMembers) {
					users.add(m);
				}
			}
		}
		return new Response<List<UserDto>>(ResponseCode.SUCCESS,"Success",new ArrayList<UserDto>(users));
	}

	@Override
	public Response<List<UserDto>> getSpecialUsers() {
		return new Response<List<UserDto>> (ResponseCode.SUCCESS,"Success",userRepo.getUsersNotInRole(Role.ROLE_MEMBER));
	}

	@Override
	public Response<UserDto> setRole(RoleSettingDto data) {
		if(data==null)
			throw new InvalidDataException("Invalid data");
		
		if(data.getUser()==null ||data.getUser().getId()==null || !userRepo.findById(data.getUser().getId()).isPresent())
			throw new InvalidDataException("Valid user required");
		
		if(data.getRole()==null)
			throw new InvalidDataException("Valid role required");
		
		User user = userRepo.findById(data.getUser().getId()).get();
		if(data.getRole()==Role.ROLE_CHAIRMAN) 
			userRepo.setRoleByRole(Role.ROLE_MEMBER.ordinal(), Role.ROLE_CHAIRMAN.ordinal());
		else if(data.getRole()==Role.ROLE_ACCOUNTANT) 
			userRepo.setRoleByRole(Role.ROLE_MEMBER.ordinal(), Role.ROLE_ACCOUNTANT.ordinal());
		else if(data.getRole()==Role.ROLE_LEADER) 
			userRepo.setRoleByRoleAndSubUnit(Role.ROLE_MEMBER.ordinal(), Role.ROLE_LEADER.ordinal(), user.getMember().getSubUnit());
		user.setRole(data.getRole());
		userRepo.save(user);
		return new Response<UserDto> (ResponseCode.SUCCESS,"Success",userRepo.findUserById(user.getId()));
	}

	@Override
	public Response<String> changePassword(ChangePasswordDto passwordChange) {
		if(passwordChange.getUsername()==null || passwordChange.getUsername().isEmpty())
			throw new InvalidDataException("Username is required");
		if(passwordChange.getNewPassword()==null || passwordChange.getNewPassword().isEmpty())
			throw new InvalidDataException("Invalid password");
		if(passwordChange.getNewPassword().length()<8)
			throw new InvalidDataException("Password too short");
		if(passwordChange.getOldPassword().contentEquals(passwordChange.getNewPassword()))
			throw new InvalidDataException("New password is the same as old");
		Authentication authentication = authenticationManager.authenticate(
        		new UsernamePasswordAuthenticationToken(
        				passwordChange.getUsername(),
        				passwordChange.getOldPassword()
        		)
			);

		Optional<User> oUser = userRepo.findByUsername(authentication.getName());
		if(oUser.isPresent()) {
			User user = oUser.get();
			user.setPassword(encoder.encode(passwordChange.getNewPassword()));
			user.setChangePassword(false);
			userRepo.save(user);
			return new Response<String>(ResponseCode.SUCCESS,"Success","Password changed successfully");
		}else
			throw new UnauthorizedException("Incorrect old password");
	}

	

}