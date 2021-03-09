package tz.mil.ngome.lms.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.uuid.Logger;
import com.google.gson.Gson;

import au.com.bytecode.opencsv.CSVReader;
import tz.mil.ngome.lms.dto.CollectReturnDto;
import tz.mil.ngome.lms.dto.CollectReturnsDto;
import tz.mil.ngome.lms.dto.CollectedReturnsResponseDto;
import tz.mil.ngome.lms.dto.DeductionsDto;
import tz.mil.ngome.lms.dto.DeductionsRequiredDto;
import tz.mil.ngome.lms.dto.DisburseLoanDto;
import tz.mil.ngome.lms.dto.DisburseLoansDto;
import tz.mil.ngome.lms.dto.LoanDenyDto;
import tz.mil.ngome.lms.dto.LoanDisburseDto;
import tz.mil.ngome.lms.dto.LoanDto;
import tz.mil.ngome.lms.dto.LoanReturnDto;
import tz.mil.ngome.lms.dto.LoansDto;
import tz.mil.ngome.lms.dto.MappedStringListDto;
import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.dto.MemberPayDto;
import tz.mil.ngome.lms.dto.TopUpDto;
import tz.mil.ngome.lms.dto.TransactionDetailDto;
import tz.mil.ngome.lms.dto.TransactionDto;
import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.Loan.LoanStatus;
import tz.mil.ngome.lms.entity.LoanReturn;
import tz.mil.ngome.lms.entity.LoanType;
import tz.mil.ngome.lms.entity.Member;
import tz.mil.ngome.lms.entity.User.Role;
import tz.mil.ngome.lms.exception.FailureException;
import tz.mil.ngome.lms.exception.InvalidDataException;
import tz.mil.ngome.lms.exception.UnauthorizedException;
import tz.mil.ngome.lms.repository.AccountRepository;
import tz.mil.ngome.lms.repository.LoanRepository;
import tz.mil.ngome.lms.repository.LoanTypeRepository;
import tz.mil.ngome.lms.repository.MemberRepository;
import tz.mil.ngome.lms.repository.TransactionRepository;
import tz.mil.ngome.lms.repository.UserRepository;
import tz.mil.ngome.lms.repository.LoanReturnRepository;
import tz.mil.ngome.lms.utils.Configuration;
import tz.mil.ngome.lms.utils.EmailSender;
import tz.mil.ngome.lms.utils.JSON;
import tz.mil.ngome.lms.utils.Report;
import tz.mil.ngome.lms.utils.Response;
import tz.mil.ngome.lms.utils.ResponseCode;

@Service
public class LoanServiceImplementation implements LoanService {
	
	@Autowired
	LoanTypeRepository loanTypeRepo;
	
	@Autowired
	LoanRepository loanRepo;
	
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	UserRepository userRepo;
	
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
	
	@Autowired
	EmailSender sender;
	
	@Autowired
	ReturnService returnService;
	
	@Override
	public Response<LoanDto> requestLoan(LoanDto loanDto) {
		Member me = userService.me().getMember();
		MemberDto member = new MemberDto();
		member.setId(me.getId());
		loanDto.setMember(member);
		loanDto.setUnit(me.getUnit());
		loanDto.setSubUnit(me.getSubUnit());
		return registerLoan(loanDto);
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
		
//		if(me.getPayAccount()==null || me.getPayAccount().isEmpty())
//			throw new InvalidDataException("Member has no pay account");
//
//		if(me.getRod()==null)
//			throw new InvalidDataException("Member ROD is not set");
		
		BeanUtils.copyProperties(loanDto, loan, "id");
		loan.setMember(me);
		loan.setAmountToPay(loanDto.getAmount());
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
		if(loanDto.getLoan()==null || loanDto.getLoan().getId()==null || loanDto.getLoan().getId().isEmpty() || !loanRepo.findById(loanDto.getLoan().getId()).isPresent())
			throw new InvalidDataException("Valid loan required");
		
		if(loanDto.getAccount()==null || loanDto.getAccount().getId()==null || loanDto.getAccount().getId().isEmpty() || !accountRepo.findById(loanDto.getAccount().getId()).isPresent())
			throw new InvalidDataException("Valid account required");
		
		if(loanDto.getDate()==null)
			throw new InvalidDataException("Invalid date");
			
		Loan loan = loanRepo.findById(loanDto.getLoan().getId()).get();
		if(loan.getStatus()!=LoanStatus.AUTHORIZED)
			throw new UnauthorizedException("Loan is not authorized");
		loan.setStatus(LoanStatus.PAID);
		loan.setEffectDate(loanDto.getDate());
		double interest = Math.floor((loan.getAmount()*loan.getInterest())/100);
		loan.setBalance(loan.getAmount()+interest);
		Loan savedLoan = loanRepo.save(loan);
		transactionService.journalLoan(savedLoan, accountRepo.findById(loanDto.getAccount().getId()).get(), loanDto.getDate());
		List<Loan> cleareds = loanRepo.getLoansByClearer(savedLoan.getId());
		if(cleareds.size()>0) {
			for(Loan cleared:cleareds) {
				transactionService.journalReturn(cleared, accountRepo.findById(loanDto.getAccount().getId()).get(), cleared.getBalance(), loanDto.getDate());
				returnService.saveValidReturn(cleared, accountRepo.findById(loanDto.getAccount().getId()).get(), cleared.getBalance(), loanDto.getDate());
			}
		}
		return new Response<LoanDto>(ResponseCode.SUCCESS,"Success",loanRepo.findLoanById(savedLoan.getId()));
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
					loan.setEffectDate(loansDto.getDate());
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
	public Response<Page<LoanDto>> getLoans(Pageable pageable) {
		if(userService.me().getRole()==Role.ROLE_LEADER)
			return new Response<Page<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getSubUnitLoansByStatus(userService.me().getMember().getSubUnit(),LoanStatus.REQUESTED,pageable));
		return new Response<Page<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getLoans(pageable));
	}
	
	@Override
	public Response<Page<LoanDto>> getLoans(int comp, Pageable pageable) {
		return new Response<Page<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getLoansByMember(comp,pageable));
	}
	
//	@Override
//	public Response<List<LoanDto>> getLoans() {
//		return new Response<List<LoanDto>>(ResponseCode.SUCCESS,"Success",loanRepo.getLoans());
//	}

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
	
	private List<DeductionsDto> getDeductions(MultipartFile file){
		List<DeductionsDto> list = new ArrayList<DeductionsDto>();
		InputStream inputStream = null;
		try {
			inputStream = file.getInputStream();
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
		for (String[] strings : objectList) {
            if (!strings[0].equals("Not set")) {
            	if(!strings[0].toLowerCase().contains("employee")) {
            		int cn = Integer.parseInt(strings[0]);
            		boolean exists = false;
            		for(DeductionsDto obj:list) {
            			if(obj.getCompNumber()==cn) {
            				exists=true;
            				List<Double> i = obj.getAmounts();
            				i.add(Double.parseDouble(strings[6]));
							Collections.sort(i,Collections.reverseOrder());
            				obj.setAmounts(i);
            			}
            		}
            		if(!exists) {
            			List<Double> i = new ArrayList<Double>();
            			i.add(Double.parseDouble(strings[6]));
            			list.add(new DeductionsDto(cn,strings[2],strings[1]+" "+strings[2]+" "+strings[3]+" "+strings[4]+" "+strings[5],i,strings[6]));
            		}
            	}
            }
		}
		return list;
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public Response<List<LoanReturnDto>> collectLoansReturns(CollectReturnsDto returnDto) {
		if(returnDto.getFile()==null || returnDto.getFile().isEmpty())
			throw new InvalidDataException("File is required");		
		if(returnDto.getAccount()==null || returnDto.getAccount().isEmpty() || !accountRepo.findById(returnDto.getAccount()).isPresent())
			throw new InvalidDataException("Valid account is required");
		if(returnDto.getDate()==null)
			throw new InvalidDataException("Date is required");
		LocalDate date = LocalDate.parse(returnDto.getDate());
		Account account = accountRepo.findById(returnDto.getAccount()).get();
		List<DeductionsDto> deductions = getDeductions(returnDto.getFile());
		List<Integer> cnDeducted = new ArrayList<Integer>();
		Logger.logInfo("Starting");
		for(DeductionsDto deduction:deductions) {
			if(memberRepo.findByCompNumber(deduction.getCompNumber()).isPresent()) {
				cnDeducted.add(deduction.getCompNumber());
				List<DeductionsRequiredDto> required = loanRepo.getDeductionsByComputerNumber(deduction.getCompNumber());
				Logger.logInfo("Member found "+memberRepo.findByCompNumber(deduction.getCompNumber()).get().getName()+" with "+required.size()+" loans to pay");
				Logger.logInfo("from LMS: "+required.toString());
				Logger.logInfo("from CC: "+deduction.getAmounts().toString());
				List<DeductionsRequiredDto> nonPaid = new ArrayList<>();
				List<Double> overPaid = new ArrayList<>();
				for(double amount:deduction.getAmounts()) {
					boolean found = false;
					for(DeductionsRequiredDto deni:required) {
						if(amount>0 && deni.getAmount()>0 && (Math.abs(deni.getAmount()-amount)<5 || deni.getBalance()==amount) && !found && !deni.isChecked()) {
							Logger.logInfo("Loan paid: "+amount);
							found=true;
							deni.setChecked(true);
							returnService.saveValidReturn(loanRepo.findById(deni.getLoan()).get(), account, amount, date);		// a correct return
						}
					}
					if(!found) {
						overPaid.add(amount);
//							returnService.saveOverReturn(memberRepo.findByCompNumber(deduction.getCompNumber()).get(), account, amount, date); // a wrong deduction for member
					}
				}
				for(DeductionsRequiredDto deni:required) {
					if(!deni.isChecked()) {
						nonPaid.add(deni);
//						returnService.saveUnderReturn(loanRepo.findById(deni.getLoan()).get(), account, deni.getAmount(), date);
					}
				}
				int size = nonPaid.size()>overPaid.size()?nonPaid.size(): overPaid.size();
				for (int i=0;i<size;i++){
					if(nonPaid.size()>i && overPaid.size()>i){
						if(nonPaid.get(i).getBalance()>=overPaid.get(i)){
							returnService.saveValidReturn(loanRepo.findById(nonPaid.get(i).getLoan()).get(),account,overPaid.get(i),date);
						}else{
							returnService.saveValidReturn(loanRepo.findById(nonPaid.get(i).getLoan()).get(),account,nonPaid.get(i).getBalance(),date);
							returnService.saveOverReturn(memberRepo.findByCompNumber(deduction.getCompNumber()).get(),account,overPaid.get(i)-nonPaid.get(i).getBalance(),date);
						}
					}else if(nonPaid.size()>i){
						returnService.saveUnderReturn(loanRepo.findById(nonPaid.get(i).getLoan()).get(),account,nonPaid.get(i).getAmount(),date);
					}else
						returnService.saveOverReturn(memberRepo.findByCompNumber(deduction.getCompNumber()).get(),account,overPaid.get(i),date);
				}
			}else {
				for(double amount:deduction.getAmounts()) {
					returnService.saveInValidReturn(deduction.getRank(),deduction.getName(), account, amount, date); // non member deduction
				}
			}
		}
		LoanStatus[] status = {LoanStatus.PAID,LoanStatus.RETURNING};
		Integer[] cn = new Integer[cnDeducted.size()];
		for(int j=0;j<cn.length;j++)
			cn[j]=cnDeducted.get(j);
		List<LoanDto> unDeducted = loanRepo.findLoansByStatusNotInArray(cn,status);
		for(LoanDto loanDto:unDeducted){
			Loan loan = loanRepo.findById(loanDto.getId()).get();
			returnService.saveUnderReturn(loan,account,loan.getReturns(),date);
		}
		return new Response<List<LoanReturnDto>>(ResponseCode.SUCCESS,"Success",loanReturnRepo.findReturnsByMonth(month(date)));
	}
	
	@Override
	public String month(LocalDate date) {
		int y = date.getYear();
		int m = date.getMonthValue();
		return m<10?y+"-0"+m:y+"-"+m;
	}

	@Override
	public Response<LoanDto> collectLoanReturn(CollectReturnDto returnDto) {
		if(returnDto.getLoan()==null || returnDto.getLoan().getId()==null || returnDto.getLoan().getId().isEmpty() || !loanRepo.findById(returnDto.getLoan().getId()).isPresent())
			throw new InvalidDataException("Invalid loan");
		if(returnDto.getAccount()==null || returnDto.getAccount().getId()==null || returnDto.getAccount().getId().isEmpty() || !accountRepo.findById(returnDto.getAccount().getId()).isPresent())
			throw new InvalidDataException("Invalid account");
		
		Loan loan = loanRepo.findById(returnDto.getLoan().getId()).get();
		
		if(loan.getStatus()!=LoanStatus.RETURNING && loan.getStatus()!=LoanStatus.PAID)
			throw new InvalidDataException("Can not collect return for provided loan");
		
		if(loan.getBalance()<returnDto.getAmount())
			throw new InvalidDataException("Amount exceeds remaining loan balance");

		returnService.saveValidReturn(loan,accountRepo.findById(returnDto.getAccount().getId()).get(),returnDto.getAmount(),returnDto.getDate());
		return new Response<LoanDto>(ResponseCode.SUCCESS,"Success",loanRepo.findLoanById(loan.getId()));
	}

	@Override
	public Response<String> cancelLoan(LoanDto loanDto) {
		if(loanDto==null || loanDto.getId()==null || loanDto.getId().isEmpty() || !loanRepo.findById(loanDto.getId()).isPresent())
			throw new InvalidDataException("Valid loan required");
		Loan loan = loanRepo.findById(loanDto.getId()).get();
		if(loan.getStatus()!=LoanStatus.PAID && loan.getStatus()!=LoanStatus.RETURNING && loan.getStatus()!=LoanStatus.COMPLETED && loan.getStatus()!=LoanStatus.CANCELED) {
			if(userService.me().getRole()==Role.ROLE_CLERK || loan.getMember().getId()==userService.me().getMember().getId()) {
				String email = "";
				String message = "Habari\n\n";
				message+="Mkopo wa TZS "+loan.getAmount()+" ulioombwa na "+loan.getMember().getName()+" na kupata kibali chako, ameusitisha muombaji mwenyewe.\n\nNgome LMS";
				if(loan.getStatus()==LoanStatus.AUTHORIZED) {
					email = userRepo.findEmailByRole(Role.ROLE_CHAIRMAN.ordinal());
				}else if(loan.getStatus()==LoanStatus.APPROVED) {
					email = userRepo.findEmailByRoleAndSubUnit(Role.ROLE_LEADER.ordinal(),loan.getMember().getSubUnit());
				}
				if(email!=null && email.length()>0)
					sender.sendMail(email, "Loan Cancelation", message);
				loan.setStatus(LoanStatus.CANCELED);
				loan.setUpdatedAt(LocalDateTime.now());
				loan.setUpdatedBy(userService.me().getId());
				loanRepo.save(loan);
				return new Response<String>(ResponseCode.SUCCESS,"Success","Loan cancelled successfully");
			}else
				throw new UnauthorizedException("You are not authorized to cancel loan");
		}else
			throw new InvalidDataException("The loan can not be canceled at this point");
	}

	@Override
	public Response<LoanDto> denyLoan(LoanDenyDto loanDto) {
		if(loanDto==null || loanDto.getId()==null || loanDto.getId().isEmpty() || !loanRepo.findById(loanDto.getId()).isPresent())
			throw new InvalidDataException("Valid loan required");
		
		if(loanDto.getReason()==null || loanDto.getReason().isEmpty())
			throw new InvalidDataException("Reason for denial is required");
		
		Loan loan = loanRepo.findById(loanDto.getId()).get();
		if((loan.getStatus()==LoanStatus.APPROVED && userService.me().getRole()==Role.ROLE_CHAIRMAN) || (loan.getStatus()==LoanStatus.REQUESTED && userService.me().getRole()==Role.ROLE_LEADER && loan.getMember().getSubUnit()==userService.me().getMember().getSubUnit())) {
			loan.setStatus(LoanStatus.DENIED);
			loan.setRemark(loanDto.getReason());
			loan.setUpdatedAt(LocalDateTime.now());
			loan.setUpdatedBy(userService.me().getId());
			loanRepo.save(loan);
			String email = userRepo.findEmailByMember(loan.getMember().getId());
			String message = "Habari\n\nMkopo wa TZS "+loan.getAmount()+" ulioumba, umekataliwa. Sababu za kukataliwa ni "+loanDto.getReason()+"\n\nNgome LMS";			
			if(email!=null && email.length()>0)
				sender.sendMail(email, "Loan Denial", message);
			return new Response<LoanDto>(ResponseCode.SUCCESS,"Success",loanRepo.findLoanById(loan.getId()));
		}else
			throw new UnauthorizedException("You are not authorized to deny the loan");
	}

	@Override
	public Response<LoanDto> updateLoan(LoanDto loanDto) {
		if(loanDto==null || loanDto.getId()==null || loanDto.getId().isEmpty() || !loanRepo.findById(loanDto.getId()).isPresent())
			throw new InvalidDataException("Valid loan required");
		
		Loan loan = loanRepo.findById(loanDto.getId()).get();
		if(loan.getMember().getId()!=userService.me().getMember().getId() || loan.getStatus()!=LoanStatus.REQUESTED)
			throw new UnauthorizedException("You are not authorized to update this loan");
		
		if(loanDto.getAmount()>0 && loanDto.getAmount()!=loan.getAmount()) {
			if(loanDto.getAmount()<loan.getLoanType().getMin() || loanDto.getAmount()>loan.getLoanType().getMax())
				throw new InvalidDataException("Invalid amount provided");
			
			loan.setAmount(loanDto.getAmount());
			loan.setAmountToPay(loanDto.getAmount());
			loan.setLoanName(loan.getLoanType().getName());
			loan.setInterest(loan.getLoanType().getInterest());
			loan.setPeriod(loan.getLoanType().getPeriod());
			loan.setPeriods(loan.getLoanType().getPeriods());
			loan.setBalance((int)Math.floor(loan.getAmount()*(1+loan.getInterest()/100)));
			loan.setReturns((int)Math.ceil(loan.getBalance()/loan.getPeriods()));			
			loan.setUpdatedAt(LocalDateTime.now());
			loan.setUpdatedBy(userService.me().getId());
			loanRepo.save(loan);
		}
		return new Response<LoanDto>(ResponseCode.SUCCESS,"Success",loanRepo.findLoanById(loan.getId()));
	}

	@Override
	public Response<LoanDto> requestTopUpLoan(TopUpDto topUpDto) {
		Member me = userService.me().getMember();
		MemberDto member = new MemberDto();
		member.setId(me.getId());
		topUpDto.setMember(member);
		topUpDto.setUnit(me.getUnit());
		topUpDto.setSubUnit(me.getSubUnit());
		return registerTopUpLoan(topUpDto);
	}

	@Override
	public Response<LoanDto> registerTopUpLoan(TopUpDto topUpDto) {
		if(topUpDto.getLoans()==null || topUpDto.getLoans().length==0)
			throw new InvalidDataException("Loan(s) to be toped up must be provided");
		
		if(topUpDto.getMember()==null || topUpDto.getMember().getId()==null || topUpDto.getMember().getId().isEmpty() || !memberRepo.findById(topUpDto.getMember().getId()).isPresent())
			throw new InvalidDataException("Invalid member provided");
		Member member = memberRepo.findById(topUpDto.getMember().getId()).get();
		int total = 0;
		for(Loan loan : topUpDto.getLoans()) {
			Optional<Loan> oLoan = loanRepo.findById(loan.getId());
			if(!oLoan.isPresent())
				throw new InvalidDataException("Invalid loan provided");
			if(oLoan.get().getMember().getId()!=member.getId())
				throw new InvalidDataException("Unrelated loan provided");
			if(oLoan.get().getStatus()!=LoanStatus.RETURNING)
				throw new InvalidDataException("Non returning loan provided");
			total+=oLoan.get().getBalance();
		}
		if(total>=topUpDto.getAmount())
			throw new InvalidDataException("Loan balance exceeds reequested amount");
		
		LoanDto dto = new LoanDto();
		BeanUtils.copyProperties(topUpDto, dto);
		Response<LoanDto> loanDto = registerLoan(dto);
		Loan loan = loanRepo.findById(loanDto.getData().getId()).get();
		loan.setUpdatedAt(LocalDateTime.now());
		loan.setUpdatedBy(userService.me().getId());
		loan.setAmountToPay(loan.getAmountToPay() - total);
		loanRepo.save(loan);
		for(Loan __loan : topUpDto.getLoans()) {
			Loan _loan = loanRepo.findById(__loan.getId()).get();
			_loan.setClearer(loan);
			_loan.setUpdatedAt(LocalDateTime.now());
			_loan.setUpdatedBy(userService.me().getId());
			loanRepo.save(_loan);
		}
		return new Response<LoanDto>(ResponseCode.SUCCESS,"Success",loanRepo.findLoanById(loan.getId()));
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public Response<List<String>> registerLoans(LoansDto loansDto) {
		String nonmember = "";
		
		if(loansDto.getFile()==null || loansDto.getFile().isEmpty())
			throw new InvalidDataException("File is required");
		
		if(loansDto.getDate()==null)
			throw new InvalidDataException("Date is required");
		
		if(loansDto.getAccount()==null || loansDto.getAccount().isEmpty() || !accountRepo.findById(loansDto.getAccount()).isPresent())
			throw new InvalidDataException("Valid account required");
		
		InputStream inputStream = null;
		try {
			inputStream = loansDto.getFile().getInputStream();
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
			e.printStackTrace();
		}
		List<LoanDto> loans = new ArrayList<LoanDto>();
		for (String[] strings : objectList) {
            if (!strings[0].equals("Not set")) {
            	if(!strings[0].toLowerCase().contains("employee")) {
            		int cn = Integer.parseInt(strings[0]);
            		Optional<Member> oMember = memberRepo.findByCompNumber(cn);
            		if(oMember.isPresent()) {
            			Member member = oMember.get();
            			try {
            				LoanDto loanDto = new LoanDto(Integer.parseInt(strings[6]),member.getId(),loanTypeRepo.findByName("Maendeleo").get(0).getId());
            				Response<LoanDto> res = this.registerLoan(loanDto);
            				Loan loan = loanRepo.findById(res.getData().getId()).get();
            				loan.setStatus(LoanStatus.AUTHORIZED);
            				loanRepo.save(loan);
            				loanDto.setId(loan.getId());
            				loans.add(loanDto);
            			}catch(Exception e) {
            				nonmember+=e.getMessage()+" for "+member.getName()+"\n";
            			}
            		}else {
            			nonmember+=strings[1]+" "+strings[2]+" "+strings[3]+" "+strings[4]+" "+strings[5]+", ";
            		}
            	}
            }
		}
        if(nonmember.length()>0) {
        	throw new FailureException(nonmember);
        }
        DisburseLoansDto disburse = new DisburseLoansDto(LocalDate.parse(loansDto.getDate()),accountRepo.findById(loansDto.getAccount()).get(),loans);
        this.disburseLoans(disburse);
        return new Response<List<String>>(ResponseCode.SUCCESS,"Success",null);
	}

	@Override
	public ResponseEntity<?> getLoansReport(LoanStatus status) {
		Configuration conf = new Configuration();
		Map<String, Object> params = new HashMap<>();
		params.put("logo", Report.logo);
		params.put("unit", conf.getUnit());
		params.put("fund", conf.getUnit()+" Relief Fund");
		String act;
		switch (status){
			case PAID:act="iliyotolewa";break;
			case REQUESTED:act="iliyoombwa";break;
			case AUTHORIZED:act="iliyoidhinishwa kutolewa";break;
			case DENIED:act="iliyokataliwa";break;
			case RETURNING:act="inayoendelea kurejeshwa";break;
			case COMPLETED:act="iliyomalizika kurejeshwa";break;
			case CANCELED:act="iliyositishwa";break;
			case APPROVED:act="iliyopitishwa na tawi/kikundi";break;
			default:act="";
		}
		params.put("title", "Mikopo "+act);
		List<LoanDisburseDto> loans = loanRepo.reportLoansStatus(status);
		Logger.logInfo("Loans processed "+String.valueOf(loans.size()));
		return Report.generate("loans", loans , params);
	}

	@Override
	public ResponseEntity<?> getLoansReport(LoanStatus status, String month) {
		Configuration conf = new Configuration();
		Map<String, Object> params = new HashMap<>();
		params.put("logo", Report.logo);
		params.put("unit", conf.getUnit());
		params.put("fund", conf.getUnit()+" Relief Fund");
		LocalDate start = LocalDate.parse(month+"-01");
		LocalDate end = start.plusMonths(1);
		end = end.minusDays(1);
		String act;
		String mwezi=start.format(DateTimeFormatter.ofPattern("MMM yyyy"));
		switch (status){
			case PAID:act="iliyolipwa";break;
			case REQUESTED:act="iliyoombwa";break;
			case AUTHORIZED:act="iliyoidhinishwa kutolewa";break;
			case DENIED:act="iliyokataliwa";break;
			case RETURNING:act="inayoanza kurejeshwa";break;
			case COMPLETED:act="iliyomalizika kurejeshwa";break;
			case CANCELED:act="iliyositishwa";break;
			case APPROVED:act="iliyopitishwa na tawi/kikundi";break;
			default:act="";
		}
		params.put("title", "Mikopo "+act+" mwezi "+mwezi);

		List<LoanDisburseDto> loans = loanRepo.reportLoansStatusOnBetweenDates(status,start,end);
		Logger.logInfo("Loans processed "+String.valueOf(loans.size()));
		return Report.generate("loans", loans , params);
	}

}
