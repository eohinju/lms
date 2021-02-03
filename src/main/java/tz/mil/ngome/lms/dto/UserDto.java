package tz.mil.ngome.lms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import tz.mil.ngome.lms.entity.User.Role;
import tz.mil.ngome.lms.repository.MemberRepository;
import tz.mil.ngome.lms.utils.SpringContext;

@Getter
@Setter
public class UserDto {
	
	@JsonIgnore
	private MemberRepository memberRepo = SpringContext.getBean(MemberRepository.class);
	
	private String id;
	private String username;
	private String email;
	private MemberDto member;
	private Role role;

	public UserDto(String id, String username, String email, String memberId, Role role) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.member = memberRepo.findMemberById(memberId);
		this.role = role;
	}
	
}
