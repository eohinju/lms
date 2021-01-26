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
import tz.mil.ngome.lms.dto.CollectReturnDto;
import tz.mil.ngome.lms.dto.CollectReturnsDto;
import tz.mil.ngome.lms.dto.CollectedReturnsResponseDto;
import tz.mil.ngome.lms.dto.DisburseLoanDto;
import tz.mil.ngome.lms.dto.DisburseLoansDto;
import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.dto.MappedStringListDto;
import tz.mil.ngome.lms.dto.MemberPayDto;
import tz.mil.ngome.lms.dto.TransactionDetailDto;
import tz.mil.ngome.lms.dto.TransactionDto;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.Loan.LoanStatus;
import tz.mil.ngome.lms.entity.LoanReturn;
import tz.mil.ngome.lms.entity.LoanType;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.exception.InvalidDataException;
import tz.mil.ngome.lms.exception.UnauthorizedException;
import tz.mil.ngome.lms.repository.AccountRepository;
import tz.mil.ngome.lms.repository.LoanRepository;
import tz.mil.ngome.lms.repository.LoanTypeRepository;
import tz.mil.ngome.lms.repository.MemberRepository;
import tz.mil.ngome.lms.repository.TransactionRepository;
import tz.mil.ngome.lms.repository.LoanReturnRepository;
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
	AccountRepository accountRepo;
	
	@Autowired
	LoanReturnRepository loanReturnRepo;
	
	@Autowired
	MemberRepository memberRepo;
	
	@Autowired
	UserService userService;
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	TransactionRepository transactionRepo;
	
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
	public Response<LoanDto> disburseLoan(DisburseLoanDto loanDto) {
		Response<LoanDto> response = new Response<LoanDto>();
		if(loanDto.getLoan()==null || loanDto.getLoan().getId()==null || loanDto.getLoan().getId().isEmpty())
			throw new InvalidDataException("Invalid Loan");
		
		if(loanDto.getAccount()==null || loanDto.getAccount().getId()==null || loanDto.getAccount().getId().isEmpty())
			throw new InvalidDataException("Account is required");
		
		if(!accountRepo.findById(loanDto.getAccount().getId()).isPresent())
			throw new InvalidDataException("Invalid account");
		
		if(loanDto.getDate()==null)
			throw new InvalidDataException("Invalid date");
			
		Optional<Loan> oLoan = loanRepo.findById(loanDto.getLoan().getId());
		if(oLoan.isPresent()) {
			Loan loan = oLoan.get();
			if(loan.getStatus()!=LoanStatus.AUTHORIZED)
				throw new UnauthorizedException("Loan is not authorized");
			loan.setStatus(LoanStatus.PAID);
			Loan savedLoan = loanRepo.save(loan);
			transactionService.journalLoan(savedLoan, accountRepo.findById(loanDto.getAccount().getId()).get(), loanDto.getDate());
			response.setCode(ResponseCode.SUCCESS);
			response.setData(loanRepo.findLoanById(savedLoan.getId()));
			return response;
		}else
			throw new InvalidDataException("Invalid Loan");
	}
	
	@Override
	public Response<List<MappedStringListDto>> disburseLoans(DisburseLoansDto loansDto) {
		if(loansDto.getAccount()==null || loansDto.getAccount().getId()==null || loansDto.getAccount().getId().isEmpty())
			throw new InvalidDataException("Account is required");
		
		if(!accountRepo.findById(loansDto.getAccount().getId()).isPresent())
			throw new InvalidDataException("Invalid account");
		
		if(loansDto.getDate()==null)
			throw new InvalidDataException("Invalid date");
		
		Optional<Loan> oLoan;
		MappedStringListDto success = new MappedStringListDto("Success");
		MappedStringListDto errors = new MappedStringListDto("Error");
		for(LoanDto loanDto : loansDto.getLoans()) {
			oLoan = loanRepo.findById(loanDto.getId());
			if(oLoan.isPresent()) {
				Loan loan = oLoan.get();
				if(loan.getStatus()==LoanStatus.AUTHORIZED) {
					loan.setStatus(LoanStatus.PAID);
					Loan savedLoan = loanRepo.save(loan);
					try {
						transactionService.journalLoan(savedLoan, accountRepo.findById(loansDto.getAccount().getId()).get(), loansDto.getDate());
						success.values.add("Disbursed loan for "+memberRepo.findNameById(loan.getMember().getId()));
					}catch(NullPointerException e) {
						loan.setStatus(LoanStatus.AUTHORIZED);
						loanRepo.save(loan);
						errors.values.add("No loan account found for "+memberRepo.findNameById(loan.getMember().getId()));
					}
				}else if(loan.getStatus()==LoanStatus.PAID)
					errors.values.add("Already paid loan for "+memberRepo.findNameById(loan.getMember().getId()));
				else
					errors.values.add("Unauthorized loan for "+memberRepo.findNameById(loan.getMember().getId()));
				
			}else
				errors.values.add("Invalid loan ["+loanDto.getId()+"]");
		}
		List<MappedStringListDto> result = new ArrayList<MappedStringListDto>();
		result.add(success);
		result.add(errors);
		return new Response<List<MappedStringListDto>>(ResponseCode.SUCCESS,"Success",result);
	}
	
	@Override
	public Response<List<LoanDto>> getLoans() {
		return new Response<List<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getAllLoans());
	}
	
	@Override
	public Response<List<LoanDto>> getLoans(int comp) {
		return new Response<List<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getLoansByMember(comp));
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
	public Response<CollectedReturnsResponseDto> collectLoansReturns(CollectReturnsDto returnDto) {
		List<MemberPayDto> success = new ArrayList<MemberPayDto>();
		List<MemberPayDto> overdeducted = new ArrayList<MemberPayDto>();
		List<MemberPayDto> undeducted = new ArrayList<MemberPayDto>();
		List<MemberPayDto> nonmember = new ArrayList<MemberPayDto>();
		
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
		Reader reader = new InputStreamReader(inputStream);
		CSVReader csvReader = new CSVReader(reader);
		try {
			objectList = csvReader.readAll();
			csvReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TransactionDto txn = new TransactionDto();
		txn.setDescription("Being return on loan collected on "+returnDto.getDate());
		txn.setDate(LocalDate.parse(returnDto.getDate()));
		List<TransactionDetailDto> txnDetails = new ArrayList<TransactionDetailDto>();
		int interest = 0;
		int repay = 0;
		boolean found;
		for (String[] strings : objectList) {
            if (!strings[0].equals("Not set")) {
            	if(!strings[0].toLowerCase().contains("employee")) {
            		int cn = Integer.parseInt(strings[0]);
            		Optional<Member> oMember = memberRepo.findByCompNumber(cn);
            		if(oMember.isPresent()) {
            			found = false;
            			Member member = oMember.get();
            			int returns = Integer.parseInt(strings[4]);
            			int balance = strings[5].contains("-")?0:Integer.parseInt(strings[5]);
            			List<LoanDto> loans = loanRepo.findLoansByMemberAndStatus(member.getId(),LoanStatus.RETURNING);
            			for(LoanDto loan : loans) {
            				if(loan.getReturns()==returns && loan.getBalance()==balance+returns) {
            					found = true;
            					if(loanReturnRepo.findByLoanAndMonth(loan.getId(),month(LocalDate.parse(returnDto.getDate()))).isEmpty()) {
            						LoanReturn loanReturn = new LoanReturn();
                					loanReturn.setAmount(returns);
                					loanReturn.setLoan(loanRepo.findById(loan.getId()).get());
                					loanReturn.setMonth(month(LocalDate.parse(returnDto.getDate())));
                					loanReturn.setCreatedBy(userService.me().getId());
                					loanReturnRepo.save(loanReturn);
                					
                					int i = (int) Math.floor(loanReturn.getAmount()*(loanReturn.getLoan().getInterest()/100));
                					interest+=i;
                					int n = loanReturn.getAmount() - i;
                					repay+=n;
                					TransactionDetailDto txnDetail = new TransactionDetailDto();
                					txnDetail.setCredit(n);
                					txnDetail.setDebit(0);
                					txnDetail.setAccount(accountRepo.findAccountByCode(cn));
                					txnDetails.add(txnDetail);
                					Loan _loan = loanRepo.findById(loan.getId()).get();
                					_loan.setBalance(loan.getBalance()-returns);
                					if(_loan.getBalance()==0)
                						_loan.setStatus(LoanStatus.COMPLETED);
                					else
                						_loan.setStatus(LoanStatus.RETURNING);
                					loanRepo.save(_loan);
                					
                					success.add(new MemberPayDto(strings[1]+" "+strings[2],Integer.parseInt(strings[4])));
            					}else
            						overdeducted.add(new MemberPayDto(strings[1]+" "+strings[2],Integer.parseInt(strings[4])));
            				}
            			}
            			if(!found)
            				overdeducted.add(new MemberPayDto(strings[1]+" "+strings[2],Integer.parseInt(strings[4])));
            		}else {
            			nonmember.add(new MemberPayDto(strings[1]+" "+strings[2],Integer.parseInt(strings[4])));
            		}
            			
            	}
            	
            }else
            	throw new InvalidDataException("No data found");
        }
		if(interest>0) {
			TransactionDetailDto txnDetail = new TransactionDetailDto();
			txnDetail.setCredit(interest);
			txnDetail.setDebit(0);
			txnDetail.setAccount(accountRepo.findAccountByName("Interest"));
			txnDetails.add(txnDetail);
			txnDetail = new TransactionDetailDto();
			txnDetail.setCredit(0);
			txnDetail.setDebit(interest+repay);
			txnDetail.setAccount(accountRepo.findAccountById(accountRepo.findById(returnDto.getAccount()).get().getId()));
			txnDetails.add(txnDetail);
			txn.setDetails(txnDetails);
			transactionService.saveTransaction(txn);			
		}
		
		List<Loan> notPaid = loanRepo.getNonReturnedLoansOnMonth(month(LocalDate.parse(returnDto.getDate())),LoanStatus.PAID.ordinal());
		notPaid.addAll(loanRepo.getNonReturnedLoansOnMonth(month(LocalDate.parse(returnDto.getDate())),LoanStatus.RETURNING.ordinal()));
		for(Loan loan:notPaid)
			undeducted.add(new MemberPayDto(loan.getMember().getServiceNumber()+" "+loan.getMember().getRank()+" "+loan.getMember().getFirstName()+" "+loan.getMember().getMiddleName()+" "+loan.getMember().getLastName(),loan.getReturns()));
		return new Response<CollectedReturnsResponseDto>(ResponseCode.SUCCESS,"Success", new CollectedReturnsResponseDto(success,overdeducted,undeducted,nonmember));
	}
	
	private String month(LocalDate date) {
		int y = date.getYear();
		int m = date.getMonthValue();
		return m<10?y+"-0"+m:y+"-"+m;
	}

	@Override
	public Response<LoanDto> collectLoanReturn(CollectReturnDto returnDto) {
		// TODO Auto-generated method stub
		return null;
	}

}
