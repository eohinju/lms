package tz.mil.ngome.lms.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.uuid.Logger;

import au.com.bytecode.opencsv.CSVReader;
import tz.mil.ngome.lms.dto.MappedStringListDto;
import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.dto.MembersImportDto;
import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.exception.DuplicateDataException;
import tz.mil.ngome.lms.exception.FailureException;
import tz.mil.ngome.lms.exception.InvalidDataException;
import tz.mil.ngome.lms.repository.AccountRepository;
import tz.mil.ngome.lms.repository.AccountTypeRepository;
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
	
	@Autowired
	AccountTypeRepository accountTypeRepo;
	
	@Autowired
	AccountRepository accountRepo;
	
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
			throw new InvalidDataException("Invalid service number is required for computer number "+memberDto.getCompNumber());
		memberDto.setServiceNumber(memberDto.getServiceNumber().toUpperCase());
		
		if(memberDto.getRank()==null || !conf.getRanks().contains(memberDto.getRank().toUpperCase()))
			throw new InvalidDataException("A valid rank is required for computer number "+memberDto.getCompNumber());
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
			throw new InvalidDataException("A valid sub unit is required for computer number "+memberDto.getCompNumber());
		
		BeanUtils.copyProperties(memberDto, member, "id");
		member.setCreatedBy(userService.me().getId());
		member.setUnit(conf.getUnit());
		member.setFirstName(member.getFirstName().trim().toUpperCase());
		member.setMiddleName(member.getMiddleName().trim().toUpperCase());
		member.setLastName(member.getLastName().trim().toUpperCase());
		
		try {
			Member savedMember = memberRepo.save(member);
			if (savedMember!=null) {
				Account account = new Account();
				account.setAccountType(accountTypeRepo.findByName("Asset").get(0));
				account.setCode(savedMember.getCompNumber());
				account.setName(savedMember.getServiceNumber()+" "+savedMember.getRank()+" "+savedMember.getFirstName()+" "+savedMember.getMiddleName()+" "+savedMember.getLastName());
				account.setCreatedBy(userService.me().getId());
				try {
					accountRepo.save(account);
				}catch(Exception e) {
					
				}
				return memberRepo.findMemberById(savedMember.getId());
			}else
				throw new FailureException("Sorry, could not save member");
		}catch(DataIntegrityViolationException e) {
			throw new DuplicateDataException("Another member exists");
		}
	}

	@Override
	public Response<Page<MemberDto>> getMembers(Pageable pageable) {
		return new Response<Page<MemberDto>> (ResponseCode.SUCCESS,"Success",memberRepo.getMembers(pageable));
	}
	
	public boolean validServiceNumber(String number) {
		number = number.trim().toUpperCase();
		if(Pattern.compile("^(P|PW|MT|MTM)( )\\d+$").matcher(number).matches())
			return true;
		return false;
	}

	@Override
	public Response<List<MappedStringListDto>> importMembers(MembersImportDto membersDto) {
		
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
		
		MappedStringListDto success = new MappedStringListDto("Success");
		MappedStringListDto duplicates = new MappedStringListDto("Duplicates");
		MappedStringListDto error = new MappedStringListDto("Error");
		for (String[] strings : objectList) {
	        if (!strings[0].equals("Not set")) {
	        	if(strings.length<8)
	        		error.values.add("Invalid number of collums for column "+strings[0]);
	        	else {
	        		try {
	        			saveMember(new MemberDto(Integer.parseInt(strings[0]),strings[1].trim(),strings[2].trim(),strings[3],strings[4],strings[5],strings[6],strings[7]));
	        			success.values.add(strings[1]+" "+strings[2]+" "+strings[3]+" "+strings[4]+" "+strings[5]);
	        		}catch(InvalidDataException e) {
	        			error.values.add(e.getMessage());
	        		}catch(DuplicateDataException e) {
	        			duplicates.values.add(e.getMessage());
	        		}catch(Exception e) {
	        			Logger.logInfo(e.getMessage());
	        		}
	        	}
	        }else
	        	throw new InvalidDataException("No data found");
	    }
		List<MappedStringListDto> result = new ArrayList<MappedStringListDto>();
		result.add(success);
		result.add(duplicates);
		result.add(error);
		return new Response<List<MappedStringListDto>>(ResponseCode.SUCCESS,"Success",result);
	}

	@Override
	public Response<MemberDto> updateMember(MemberDto memberDto) {
		if(memberDto==null || memberDto.getId()==null || memberDto.getId().isEmpty() || !memberRepo.findById(memberDto.getId()).isPresent())
			throw new InvalidDataException("Invalid member");
		
		Member member = memberRepo.findById(memberDto.getId()).get();
		if(memberDto.getServiceNumber()!=null && !memberDto.getServiceNumber().contentEquals(member.getServiceNumber())) {
			if(!validServiceNumber(memberDto.getServiceNumber()))
				throw new InvalidDataException("Invalid service number");
			if(memberRepo.findByServiceNumber(memberDto.getServiceNumber()).isPresent())
				throw new DuplicateDataException("Service number is already used");
			member.setServiceNumber(memberDto.getServiceNumber());
		}
		
		if(memberDto.getRank()!=null && !memberDto.getRank().contentEquals(member.getRank())) {
			if(!conf.getRanks().contains(memberDto.getRank().toUpperCase()))
				throw new InvalidDataException("Invalid rank");
			member.setRank(memberDto.getRank().toUpperCase());
		}
		
		if(memberDto.getSubUnit()!=null && !memberDto.getSubUnit().contentEquals(member.getSubUnit())) {
			if(!conf.getSubUnits().contains(memberDto.getSubUnit()))
				throw new InvalidDataException("Invalid sub unit");
			member.setSubUnit(memberDto.getSubUnit());
		}
		
		if(memberDto.getFirstName()!=null && !memberDto.getFirstName().isEmpty())
			member.setFirstName(memberDto.getFirstName().trim().toUpperCase());
		
		if(memberDto.getMiddleName()!=null && !memberDto.getMiddleName().isEmpty())
			member.setMiddleName(memberDto.getMiddleName().trim().toUpperCase());
		
		if(memberDto.getLastName()!=null && !memberDto.getLastName().isEmpty())
			member.setLastName(memberDto.getLastName().trim().toUpperCase());
		
		if(memberDto.getPhone()!=null && !memberDto.getPhone().isEmpty())
			member.setPhone(memberDto.getPhone().trim());
		
		memberRepo.save(member);
		return new Response<MemberDto>(ResponseCode.SUCCESS,"Success",memberRepo.findMemberById(member.getId()));
	}

}
