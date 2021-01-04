package tz.mil.ngome.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.service.MemberService;

@CrossOrigin( origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "api/")
public class MemberController {

	@Autowired
	MemberService memberService;

	@PostMapping(value = "register-member", consumes = MediaType.APPLICATION_JSON_VALUE)
	private Member registerMember(@RequestBody MemberDto memberDto) {
		return this.memberService.registerMember(memberDto);
	}
	
}
