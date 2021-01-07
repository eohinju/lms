package tz.mil.ngome.lms.service;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface MemberService {

	public Response<Member> registerMember(MemberDto memberDto);
	
}
