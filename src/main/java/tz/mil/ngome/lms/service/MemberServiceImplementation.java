package tz.mil.ngome.lms.service;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.repository.MemberRepository;
import tz.mil.ngome.lms.utils.Response;
import tz.mil.ngome.lms.utils.ResponseCode;

@Service
public class MemberServiceImplementation implements MemberService {

	@Autowired
	MemberRepository memberRepo;
	
	@Override
	public Response<Member> registerMember(MemberDto memberDto) {
		Response<Member> response = new Response<Member>();
		Member member = new Member();
		if(memberDto.getFirstName()==null || memberDto.getFirstName().isBlank() 
				|| memberDto.getMiddleName()==null || memberDto.getMiddleName().isBlank() 
				|| memberDto.getLastName()==null || memberDto.getLastName().isBlank()) {
			response.setCode(ResponseCode.INCOMPLETE_DATA);
			response.setMessage("Provide all three names");
			return response;
		}
		BeanUtils.copyProperties(memberDto, member, "id");
		member.setCreatedBy("System");
		try {
			Member savedMember = memberRepo.save(member);
			if (savedMember!=null) {
				response.setData(savedMember);
				response.setCode(ResponseCode.SUCCESS);
			}else {
				response.setData(null);
				response.setCode(ResponseCode.FAILURE);
				response.setMessage("Sorry, could not register member");
			}
		}catch(DataIntegrityViolationException e) {
			response.setData(null);
			response.setCode(ResponseCode.DUPLICATE_DATA);
			response.setMessage("Sorry, could not register member. Another member exists");
		}
		return response;
	}

}
