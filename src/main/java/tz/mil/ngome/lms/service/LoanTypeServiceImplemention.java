package tz.mil.ngome.lms.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.LoanTypeDto;
import tz.mil.ngome.lms.entity.LoanType;
import tz.mil.ngome.lms.entity.LoanType.Period;
import tz.mil.ngome.lms.exception.InvalidDataException;
import tz.mil.ngome.lms.repository.LoanTypeRepository;
import tz.mil.ngome.lms.utils.Response;
import tz.mil.ngome.lms.utils.ResponseCode;

@Service
public class LoanTypeServiceImplemention implements LoanTypeService {

	@Autowired
	UserService userService;
	
	@Autowired
	LoanTypeRepository loanTypeRepo;
	
	@Override
	public Response<LoanTypeDto> createLoanType(LoanTypeDto loanTypeDto) {
		Response<LoanTypeDto> response = new Response<LoanTypeDto>();
		
		if(loanTypeDto.getName()==null || loanTypeDto.getName().isEmpty())
			throw new InvalidDataException("Name is required");
		
		if(loanTypeDto.getInterest()<0)
			throw new InvalidDataException("Invalid interest rate");
		
		if(loanTypeDto.getPeriods()<=0)
			throw new InvalidDataException("Invalid return periods");
		
		if(loanTypeDto.getMin()==loanTypeDto.getMax() || loanTypeDto.getMin()<0)
			throw new InvalidDataException("Invalid limits given");
		
		if(loanTypeDto.getPeriod()==null)
			throw new InvalidDataException("Period is required");
		
		LoanType loanType = new LoanType();
		String[] ignore = {"id"};
		BeanUtils.copyProperties(loanTypeDto, loanType,ignore);
//		switch(loanTypeDto.getPeriod()) {
//			case 1:	loanType.setPeriod(Period.DAY); break;
//			case 2: loanType.setPeriod(Period.WEEK); break;
//			case 3: loanType.setPeriod(Period.MONTH); break;
//			case 4: loanType.setPeriod(Period.YEAR);break;
//			default: throw new InvalidDataException("Invalid period");
//						
//		}
		loanType.setCreatedBy(userService.me().getId());
		LoanType savedLoadType = loanTypeRepo.save(loanType);
		BeanUtils.copyProperties(savedLoadType, loanTypeDto);
		response.setCode(ResponseCode.SUCCESS);
		response.setData(loanTypeDto);
		return response;
	}

	@Override
	public Response<List<LoanTypeDto>> getLoanTypes() {
		return new Response<List<LoanTypeDto>> (ResponseCode.SUCCESS,"Success",loanTypeRepo.getLoanTypes());
	}

}
