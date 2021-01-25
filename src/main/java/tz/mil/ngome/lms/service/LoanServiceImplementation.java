package tz.mil.ngome.lms.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;
import tz.mil.ngome.lms.dto.CollectReturnsDto;
import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.Loan.LoanStatus;
import tz.mil.ngome.lms.entity.LoanReturn;
import tz.mil.ngome.lms.entity.LoanType;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.exception.InvalidDataException;
import tz.mil.ngome.lms.exception.UnauthorizedException;
import tz.mil.ngome.lms.repository.LoanRepository;
import tz.mil.ngome.lms.repository.LoanTypeRepository;
import tz.mil.ngome.lms.repository.MemberRepository;
import tz.mil.ngome.lms.repository.loanReturnRepository;
import tz.mil.ngome.lms.utils.Response;
import tz.mil.ngome.lms.utils.ResponseCode;

@Service
public class LoanServiceImplementation implements LoanService {

	private static final Logger logger = LoggerFactory.getLogger(LoanService.class);
	
	@Autowired
	LoanTypeRepository loanTypeRepo;
	
	@Autowired
	LoanRepository loanRepo;
	
	@Autowired
	loanReturnRepository loanReturnRepo;
	
	@Autowired
	MemberRepository memberRepo;
	
	@Autowired
	UserService userService;
	
	@Override
	public Response<LoanDto> requestLoan(LoanDto loanDto) {
		Response<LoanDto> response = new Response<LoanDto>();
		
		if(loanDto.getLoanType()==null)
			throw new InvalidDataException("Loan type required");
		
		if(loanDto.getLoanType().getId()==null || !loanTypeRepo.findById(loanDto.getLoanType().getId()).isPresent() )
			throw new InvalidDataException("Invalid loan type provided");
		
		LoanType type = loanTypeRepo.findById(loanDto.getLoanType().getId()).get();
		if(loanDto.getAmount()<type.getMin() || loanDto.getAmount()>type.getMax())
			throw new InvalidDataException("Invalid amount provided");
		
		Loan loan = new Loan();
		Member me = userService.me().getMember();
		
		BeanUtils.copyProperties(loanDto, loan, "id");
		loan.setMember(me);
		loan.setLoanType(loanTypeRepo.findById(loanDto.getLoanType().getId()).get());
		loan.setUnit(me.getUnit());
		loan.setSubUnit(me.getSubUnit());
		loan.setLoanName(type.getName());
		loan.setInterest(type.getInterest());
		loan.setPeriod(type.getPeriod());
		loan.setPeriods(type.getPeriods());
		loan.setBalance((int)Math.floor(loan.getAmount()*(1+loan.getInterest()/100)));
		loan.setReturns((int)Math.ceil(loan.getBalance()/loan.getPeriods()));
		loan.setStatus(LoanStatus.REQUESTED);
		loan.setCreatedBy(me.getId());
		Loan savedLoan = loanRepo.save(loan);
		loanDto = loanRepo.findLoanById(savedLoan.getId());
		response.setCode(ResponseCode.SUCCESS);
		response.setData(loanDto);
		return response;
	}

	@Override
	public Response<LoanDto> registerLoan(LoanDto loanDto) {
		Response<LoanDto> response = new Response<LoanDto>();
		
		if(loanDto.getLoanType()==null)
			throw new InvalidDataException("Loan type required");
		System.out.println(loanDto.getLoanType().getId());
		if(loanDto.getLoanType().getId()==null || !loanTypeRepo.findById(loanDto.getLoanType().getId()).isPresent() )
			throw new InvalidDataException("Invalid loan type provided");
		
		if(loanDto.getMember()==null)
			throw new InvalidDataException("Member required");
		
		if(loanDto.getMember().getId()==null || !memberRepo.findById(loanDto.getMember().getId()).isPresent() )
			throw new InvalidDataException("Invalid member provided");
		
		LoanType type = loanTypeRepo.findById(loanDto.getLoanType().getId()).get();
		System.out.println(type.getMax());
		System.out.println(type.getMin());
		
		if(loanDto.getAmount()<type.getMin() || loanDto.getAmount()>type.getMax())
			throw new InvalidDataException("Invalid amount provided");
		
		Loan loan = new Loan();
		Member me = memberRepo.findById(loanDto.getMember().getId()).get();
		
		BeanUtils.copyProperties(loanDto, loan, "id");
		loan.setMember(me);
		loan.setUnit(me.getUnit());
		loan.setLoanType(loanTypeRepo.findById(loanDto.getLoanType().getId()).get());
		loan.setSubUnit(me.getSubUnit());
		loan.setLoanName(type.getName());
		loan.setInterest(type.getInterest());
		loan.setPeriod(type.getPeriod());
		loan.setPeriods(type.getPeriods());
		loan.setBalance((int)Math.floor(loan.getAmount()*(1+loan.getInterest()/100)));
		loan.setReturns((int)Math.ceil(loan.getBalance()/loan.getPeriods()));
		loan.setStatus(LoanStatus.REQUESTED);
		loan.setCreatedBy(userService.me().getId());
		Loan savedLoan = loanRepo.save(loan);
		loanDto = loanRepo.findLoanById(savedLoan.getId());
		response.setCode(ResponseCode.SUCCESS);
		response.setData(loanDto);
		return response;
	}

	@Override
	public Response<LoanDto> approveLoan(LoanDto loanDto) {
		Response<LoanDto> response = new Response<LoanDto>();
		if(loanDto.getId()==null || loanDto.getId().isEmpty())
			throw new InvalidDataException("Invalid Loan");
		Optional<Loan> oLoan = loanRepo.findById(loanDto.getId());
		if(oLoan.isPresent()) {
			Loan loan = oLoan.get();
			if(loan.getStatus()!=LoanStatus.REQUESTED)
				throw new UnauthorizedException("Loan can not be approved");
			loan.setStatus(LoanStatus.APPROVED);
			Loan savedLoan = loanRepo.save(loan);
			BeanUtils.copyProperties(savedLoan, loanDto);
			response.setCode(ResponseCode.SUCCESS);
			response.setData(loanDto);
			return response;
		}else
			throw new InvalidDataException("Invalid Loan");
	}

	@Override
	public Response<LoanDto> authorizeLoan(LoanDto loanDto) {
		Response<LoanDto> response = new Response<LoanDto>();
		if(loanDto.getId()==null || loanDto.getId().isEmpty())
			throw new InvalidDataException("Invalid Loan");
		Optional<Loan> oLoan = loanRepo.findById(loanDto.getId());
		if(oLoan.isPresent()) {
			Loan loan = oLoan.get();
			if(loan.getStatus()!=LoanStatus.APPROVED)
				throw new UnauthorizedException("Loan can not be authorized");
			loan.setStatus(LoanStatus.AUTHORIZED);
			Loan savedLoan = loanRepo.save(loan);
			BeanUtils.copyProperties(savedLoan, loanDto);
			response.setCode(ResponseCode.SUCCESS);
			response.setData(loanDto);
			return response;
		}else
			throw new InvalidDataException("Invalid Loan");
	}

	@Override
	public Response<LoanDto> disburseLoan(LoanDto loanDto) {
		Response<LoanDto> response = new Response<LoanDto>();
		if(loanDto.getId()==null || loanDto.getId().isEmpty())
			throw new InvalidDataException("Invalid Loan");
		Optional<Loan> oLoan = loanRepo.findById(loanDto.getId());
		if(oLoan.isPresent()) {
			Loan loan = oLoan.get();
			if(loan.getStatus()!=LoanStatus.AUTHORIZED)
				throw new UnauthorizedException("Loan can not be authorized");
			loan.setStatus(LoanStatus.PAID);
			Loan savedLoan = loanRepo.save(loan);
			BeanUtils.copyProperties(savedLoan, loanDto);
			response.setCode(ResponseCode.SUCCESS);
			response.setData(loanDto);
			return response;
		}else
			throw new InvalidDataException("Invalid Loan");
	}	
	
	@Override
	public Response<List<LoanDto>> getLoans() {
		return new Response<List<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getAllLoans());
	}

	@Override
	public Response<List<LoanDto>> getRequestedLoans(String subUnit) {
		return new Response<List<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getLoansBySubUnitAndStatus(subUnit, LoanStatus.REQUESTED));
	}

	@Override
	public Response<List<LoanDto>> getApprovedLoans() {
		return new Response<List<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getLoansByStatus(LoanStatus.APPROVED));
	}

	@Override
	public Response<List<LoanDto>> getAuthorizedLoans() {
		return new Response<List<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getLoansByStatus(LoanStatus.AUTHORIZED));
	}

	@Override
	public Response<List<LoanDto>> getDisbursedLoans() {
		return new Response<List<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getLoansByStatus(LoanStatus.PAID));
	}
	
	@Override
	public Response<List<LoanDto>> getIncompleteDisbursedLoans() {
		return new Response<List<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getLoansByStatus(LoanStatus.PAID));
	}

	@Override
	public Response<List<LoanDto>> collectLoansReturns(CollectReturnsDto returnDto) {
		Response<List<LoanDto>> response = new Response<List<LoanDto>>();
		
		if(returnDto.getFile()==null || returnDto.getFile().isEmpty())
			throw new InvalidDataException("File is required");
		
		if(returnDto.getDate()==null)
			throw new InvalidDataException("Date is required");
		InputStream inputStream = null;
		try {
			inputStream = returnDto.getFile().getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidDataException("File could not be read");
		}
		
		List<String[]> objectList = new ArrayList<>();
        try (Reader reader = new InputStreamReader(inputStream)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                objectList = csvReader.readAll();
                for (String[] strings : objectList) {
                    if (!strings[0].equals("Not set")) {
                    	if(!strings[0].toLowerCase().contains("employee")) {
                    		int cn = Integer.parseInt(strings[0]);
                    		Optional<Member> oMember = memberRepo.findByCompNumber(cn);
                    		if(oMember.isPresent()) {
                    			boolean found = false;
                    			Member member = oMember.get();
                    			LocalDate effectDate = LocalDate.parse(strings[3]);
                    			int returns = Integer.parseInt(strings[4]);
                    			int balance = strings[5].contains("-")?0:Integer.parseInt(strings[5]);
                    			int loanAmount = strings[6].contains("-")?0:Integer.parseInt(strings[6]);
                    			List<LoanDto> loans = loanRepo.findLoansByMemberAndStatus(member.getId(),LoanStatus.RETURNING);
                    			for(LoanDto loan : loans) {
                    				if(loan.getBalance()==balance+returns) {
                    					LoanReturn loanReturn = new LoanReturn();
                    					loanReturn.setAmount(returns);
                    					loanReturn.setLoan(loanRepo.findById(loan.getId()).get());
                    					loanReturn.setMonth(month(LocalDate.parse(returnDto.getDate())));
                    					loanReturn.setCreatedBy(userService.me().getId());
                    					loanReturnRepo.save(loanReturn);
                    					Loan _loan = loanRepo.findById(loan.getId()).get();
                    					_loan.setBalance(loan.getBalance()-returns);
                    					loanRepo.save(_loan);
                    					found = true;
                    				}
                    			}
                    			if(!found)
                    				throw new InvalidDataException(member.getFirstName()+" "+member.getMiddleName()+" "+member.getLastName()+" hana mkopo wenye makato ya "+returns);
                    		}else
                    			logger.info("Member not found with comp # "+strings[0]);
                    	}
                    	
                    }else
                    	throw new InvalidDataException("No data found");
                }
            }catch(Exception e) {
            	throw new InvalidDataException("CSV not read");
            }
        }catch(IOException e) {
        	throw new InvalidDataException("Input stream not read");
        }
		return response;
	}
	
	private String month(LocalDate date) {
		int y = date.getYear();
		int m = date.getMonthValue();
		return m<10?y+"-0"+m:y+"-"+m;
	}

}
