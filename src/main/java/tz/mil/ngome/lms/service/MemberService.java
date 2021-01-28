package tz.mil.ngome.lms.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.MappedStringListDto;
import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.dto.MembersImportDto;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface MemberService {

	public Response<MemberDto> registerMember(MemberDto memberDto);
	
	public Response<MemberDto> updateMember(MemberDto memberDto);

	public Response<List<MappedStringListDto>> importMembers(MembersImportDto file);
	
	public Response<Page<MemberDto>> getMembers(Pageable pageable);
	
	public Response<List<MemberDto>> findMember(String data);
	
}
