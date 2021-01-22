package tz.mil.ngome.lms.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.exception.DuplicateDataException;
import tz.mil.ngome.lms.exception.FailureException;
import tz.mil.ngome.lms.exception.InvalidDataException;
import tz.mil.ngome.lms.repository.MemberRepository;
import tz.mil.ngome.lms.utils.Configuration;
import tz.mil.ngome.lms.utils.Response;
import tz.mil.ngome.lms.utils.ResponseCode;

@Service
public class MemberServiceImplementation implements MemberService {

	@Autowired
	UserService userService;
	
	@Autowired
	MemberRepository memberRepo;
	
	Configuration conf = new Configuration();

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLERK')")
	@Override
	public Response<MemberDto> registerMember(MemberDto memberDto) {
		Response<MemberDto> response = new Response<MemberDto>();
		Member member = new Member();
		
		if(memberDto.getCompNumber()==0)
			throw new InvalidDataException("Computer number is required");
		
		if(memberDto.getServiceNumber()==null || memberDto.getServiceNumber().trim().isEmpty())
			throw new InvalidDataException("Service number is required");
		
		if(!validServiceNumber(memberDto.getServiceNumber()))
			throw new InvalidDataException("Invalid service number");
		memberDto.setServiceNumber(memberDto.getServiceNumber().toUpperCase());
		
		if(memberDto.getRank()==null || !conf.getRanks().contains(memberDto.getRank().toUpperCase()))
			throw new InvalidDataException("A valid rank is required");
		memberDto.setRank(memberDto.getRank().toUpperCase());
		
		if(memberRepo.findByServiceNumber(memberDto.getServiceNumber()).isPresent())
			throw new DuplicateDataException("Service number is already used");
		
		if(memberRepo.findByCompNumber(memberDto.getCompNumber()).isPresent())
			throw new DuplicateDataException("Computer number is already used");
		
		if(memberDto.getFirstName()==null || memberDto.getFirstName().isEmpty() 
				|| memberDto.getMiddleName()==null || memberDto.getMiddleName().isEmpty() 
				|| memberDto.getLastName()==null || memberDto.getLastName().isEmpty()) 
			throw new InvalidDataException("All three names are required");
		
		if(memberDto.getSubUnit()==null || !conf.getSubUnits().contains(memberDto.getSubUnit()))
			throw new InvalidDataException("A valid sub unit is required");
		
		BeanUtils.copyProperties(memberDto, member, "id");
		member.setCreatedBy(userService.me().getId());
		member.setUnit(conf.getUnit());
		
		try {
			Member savedMember = memberRepo.save(member);
			if (savedMember!=null) {
				response.setData(memberRepo.findMemberById(savedMember.getId()));
				response.setCode(ResponseCode.SUCCESS);
			}else
				throw new FailureException("Sorry, could not save member");
		}catch(DataIntegrityViolationException e) {
			throw new DuplicateDataException("Another member exists");
		}
		return response;
	}

	@Override
	public Response<List<MemberDto>> getMembers() {
		return new Response<List<MemberDto>> (ResponseCode.SUCCESS,"Success",memberRepo.getMembers());
	}
	
	public boolean validServiceNumber(String number) {
		number = number.trim().toUpperCase();
		if(Pattern.compile("^(P|PW|MT|MTM)( )\\d+$").matcher(number).matches())
			return true;
		return false;
	}

}
