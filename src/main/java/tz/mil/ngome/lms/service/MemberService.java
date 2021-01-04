package tz.mil.ngome.lms.service;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.entity.Member;

@Service
public interface MemberService {

	public Member registerMember(MemberDto memberDto);
	
}
