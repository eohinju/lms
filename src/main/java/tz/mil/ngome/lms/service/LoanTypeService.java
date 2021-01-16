package tz.mil.ngome.lms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.LoanTypeDto;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface LoanTypeService {

	Response<LoanTypeDto> createLoanType(LoanTypeDto loadTypeDto);
	Response<List<LoanTypeDto>> getLoanTypes();
	
}
