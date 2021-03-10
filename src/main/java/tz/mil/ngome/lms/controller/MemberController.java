package tz.mil.ngome.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.web.multipart.MultipartFile;
import tz.mil.ngome.lms.dto.*;
import tz.mil.ngome.lms.service.MemberService;
import tz.mil.ngome.lms.utils.Configuration;
import tz.mil.ngome.lms.utils.Response;
import tz.mil.ngome.lms.utils.ResponseCode;

@CrossOrigin( origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "api/")
public class MemberController {

	@Autowired
	MemberService memberService;
	
	Configuration conf = new Configuration();

	@PostMapping(value = "register-member", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<MemberDto> registerMember(@RequestBody MemberDto memberDto) {
		return this.memberService.registerMember(memberDto);
	}
	
	@PostMapping(value = "update-member", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<MemberDto> updateMember(@RequestBody MemberDto memberDto) {
		return this.memberService.updateMember(memberDto);
	}
	
	@PostMapping(value = "update-profile", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<UserDto> updateProfile(@RequestBody ProfileDto profileDto) {
		return this.memberService.updateProfile(profileDto);
	}
	
	@PostMapping(value = "import-members-form")
	private Response<List<MappedStringListDto>> importMembers(@ModelAttribute MembersImportDto membersDto) {
		return this.memberService.importMembers(membersDto);
	}

	@PostMapping(value = "import-members",  consumes = MediaType.APPLICATION_JSON_VALUE)
	private Response<List<MappedStringListDto>> importMembers(@RequestBody FileBytesDto fileBytesDto) {
		return this.memberService.importMembers(new MembersImportDto(new MockMultipartFile("file",fileBytesDto.getFile())));
	}
	
	@GetMapping(value = "get-sub-units")
	private Response<List<String>> getSubUnits() {
		return new Response<List<String>>(ResponseCode.SUCCESS,"Success",this.conf.getSubUnits());
	}
	
	@GetMapping(value = "get-ranks")
	private Response<List<String>> getRanks() {
		return new Response<List<String>>(ResponseCode.SUCCESS,"Success",this.conf.getRanks());
	}
	
	@GetMapping(value = "get-members")
	private Response<Page<MemberDto>> getMembers(@RequestParam(name = "page", required = false) Integer page,@RequestParam(name = "size", required = false) Integer size) {
		if(page==null || size==null)
			return this.memberService.getMembers(PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("compNumber"))));
		int p = page.intValue()<0?0:page.intValue();
		int s = size.intValue()<0?conf.getDefaultPageSize():size.intValue();
		return this.memberService.getMembers(PageRequest.of(p, s, Sort.by(Sort.Order.asc("compNumber"))));
	}
	
	@GetMapping(value = "get-members/{data}")
	private Response<List<MemberDto>> findMember(@PathVariable(name = "data", required = true) String data) {
		data = data==null?"":data;
		return this.memberService.findMember(data);
	}
	
}
