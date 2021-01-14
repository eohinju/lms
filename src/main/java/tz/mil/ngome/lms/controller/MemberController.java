package tz.mil.ngome.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tz.mil.ngome.lms.dto.MemberDto;
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
	
	@GetMapping(value = "get-sub-units")
	private Response<List<String>> getSubUnits() {
		return new Response<List<String>>(ResponseCode.SUCCESS,"",this.conf.getSubUnits());
	}
	
	@GetMapping(value = "get-members")
	private Response<List<MemberDto>> getMembers() {
		return this.memberService.getMembers();
	}
	
}
