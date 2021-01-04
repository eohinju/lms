package tz.mil.ngome.lms.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.repository.MemberRepository;

@Service
public class MemberServiceImplementation implements MemberService {

	@Autowired
	MemberRepository memberRepo;
	
	@Override
	public Member registerMember(MemberDto memberDto) {
		Member member = new Member();
		BeanUtils.copyProperties(memberDto, member, "id");
		member.setCreatedBy("System");
		Member savedMember = memberRepo.save(member);
		return savedMember;
	}

}
