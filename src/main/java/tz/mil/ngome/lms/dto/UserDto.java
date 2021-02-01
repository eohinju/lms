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
	private MemberDto member;
	private Role role;

	public UserDto(String id, String username, String memberId, Role role) {
		this.id = id;
		this.username = username;
		this.member = memberRepo.findMemberById(memberId);
		this.role = role;
	}
	
}
