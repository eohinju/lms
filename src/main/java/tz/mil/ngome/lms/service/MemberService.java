package tz.mil.ngome.lms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface MemberService {

	public Response<MemberDto> registerMember(MemberDto memberDto);

	public Response<List<MemberDto>> getMembers();
	
}
