package tz.mil.ngome.lms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.dto.MembersImportDto;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface MemberService {

	public Response<MemberDto> registerMember(MemberDto memberDto);

	public Response<List<MemberDto>> importMembers(MembersImportDto file);
	
	public Response<List<MemberDto>> getMembers();
	
}
