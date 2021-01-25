package tz.mil.ngome.lms.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.uuid.Logger;

import au.com.bytecode.opencsv.CSVReader;
import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.dto.MembersImportDto;
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
		response.setCode(ResponseCode.SUCCESS);
		response.setData(saveMember(memberDto));
		return response;
	}
	
	public MemberDto saveMember(MemberDto memberDto) {
		Member member = new Member();
		
		if(memberDto.getCompNumber()==0)
			throw new InvalidDataException("Computer number is required");
		
		if(memberDto.getServiceNumber()==null || memberDto.getServiceNumber().trim().isEmpty())
			throw new InvalidDataException("Service number is required");
		
		if(!validServiceNumber(memberDto.getServiceNumber()))
			throw new InvalidDataException("Invalid service number "+memberDto.getServiceNumber());
		memberDto.setServiceNumber(memberDto.getServiceNumber().toUpperCase());
		
		if(memberDto.getRank()==null || !conf.getRanks().contains(memberDto.getRank().toUpperCase()))
			throw new InvalidDataException("A valid rank is required");
		memberDto.setRank(memberDto.getRank().toUpperCase());
		
		if(memberRepo.findByServiceNumber(memberDto.getServiceNumber()).isPresent())
			throw new DuplicateDataException("Service number "+memberDto.getServiceNumber()+" is already used");
		
		if(memberRepo.findByCompNumber(memberDto.getCompNumber()).isPresent())
			throw new DuplicateDataException("Computer number "+memberDto.getCompNumber()+" is already used");
		
		if(memberDto.getFirstName()==null || memberDto.getFirstName().isEmpty() 
				|| memberDto.getMiddleName()==null || memberDto.getMiddleName().isEmpty() 
				|| memberDto.getLastName()==null || memberDto.getLastName().isEmpty()) 
			throw new InvalidDataException("All three names are required for computer number "+memberDto.getCompNumber());
		
		if(memberDto.getSubUnit()==null || !conf.getSubUnits().contains(memberDto.getSubUnit()))
			throw new InvalidDataException("A valid sub unit is required");
		
		BeanUtils.copyProperties(memberDto, member, "id");
		member.setCreatedBy(userService.me().getId());
		member.setUnit(conf.getUnit());
		
		try {
			Member savedMember = memberRepo.save(member);
			if (savedMember!=null) {
				return memberRepo.findMemberById(savedMember.getId());
			}else
				throw new FailureException("Sorry, could not save member");
		}catch(DataIntegrityViolationException e) {
			throw new DuplicateDataException("Another member exists");
		}
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

	@Override
	public Response<List<MemberDto>> importMembers(MembersImportDto membersDto) {
		
		if(membersDto==null || membersDto.getFile()==null || membersDto.getFile().isEmpty())
			throw new InvalidDataException("No file provided");
		
		InputStream inputStream = null;
		try {
			inputStream = membersDto.getFile().getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidDataException("File could not be read");
		}
		List<MemberDto> registeredMembers = new ArrayList<MemberDto>();
		List<String[]> objectList = new ArrayList<>();
		Reader reader = new InputStreamReader(inputStream);
		CSVReader csvReader = new CSVReader(reader);
		try {
			objectList = csvReader.readAll();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    for (String[] strings : objectList) {
	        if (!strings[0].equals("Not set")) {
	        	if(strings.length<8)
	          		throw new InvalidDataException("Invalid number of collumns");
	          	registeredMembers.add(saveMember(new MemberDto(Integer.parseInt(strings[0]),strings[1].trim(),strings[2].trim(),strings[3],strings[4],strings[5],strings[6],strings[7])));
	        }else
	        	throw new InvalidDataException("No data found");
	    }
	    Response<List<MemberDto>> response = new Response<List<MemberDto>>();
		response.setCode(ResponseCode.SUCCESS);
		response.setData(registeredMembers);
		return response;
	}

}
