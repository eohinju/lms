package tz.mil.ngome.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.dto.MembersImportDto;
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
	
	@PostMapping(value = "import-members")
	private Response<List<MemberDto>> importMembers(@ModelAttribute MembersImportDto membersDto) {
		return this.memberService.importMembers(membersDto);
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
	private Response<List<MemberDto>> getMembers() {
		return this.memberService.getMembers();
	}
	
}
