//package tz.co.emmapho.duka.service;
//
//import java.sql.Date;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import javax.persistence.EntityManager;
//import javax.transaction.Transactional;
//
//import org.hibernate.transform.Transformers;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.interceptor.TransactionAspectSupport;
//
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import com.github.openjson.JSONArray;
//import com.github.openjson.JSONObject;
//
//import tz.co.emmapho.duka.dto.PartnerDto;
//import tz.co.emmapho.duka.dto.SaleClientDto;
//import tz.co.emmapho.duka.dto.SaleDateDto;
//import tz.co.emmapho.duka.dto.SaleDto;
//import tz.co.emmapho.duka.dto.SaleProductDto;
//import tz.co.emmapho.duka.dto.SalesReportDto;
//import tz.co.emmapho.duka.dto.StockIssueDto;
//import tz.co.emmapho.duka.dto.StockMoveReportDto;
//import tz.co.emmapho.duka.dto.StockReceiptDto;
//import tz.co.emmapho.duka.dto.TransactionDto;
//import tz.co.emmapho.duka.dto.TrialBalanceDto;
//import tz.co.emmapho.duka.entity.Account;
//import tz.co.emmapho.duka.entity.Partner;
//import tz.co.emmapho.duka.entity.Product;
//import tz.co.emmapho.duka.entity.Transaction;
//import tz.co.emmapho.duka.entity.Transaction.TransactionType;
//import tz.co.emmapho.duka.exception.InvalidDataException;
//import tz.co.emmapho.duka.entity.TransactionDetail;
//import tz.co.emmapho.duka.repo.AccountRepo;
//import tz.co.emmapho.duka.repo.PartnerRepo;
//import tz.co.emmapho.duka.repo.ProductRepo;
//import tz.co.emmapho.duka.repo.TransactionDetailRepo;
//import tz.co.emmapho.duka.repo.TransactionRepo;
//import tz.co.emmapho.duka.utils.Response;
//import tz.co.emmapho.duka.utils.ResponseCode;
//
//@Service
//public class TransactionServiceImpl implements TransactionService {
//
//	@Autowired
//	TransactionRepo transactionRepo;
//	
//	@Autowired
//	TransactionDetailRepo detailRepo;
//	
//	@Autowired
//	PartnerRepo partnerRepo;
//	
//	@Autowired
//	ProductRepo productRepo;
//	
//	@Autowired
//	AccountRepository accountRepo;
//	
//	@Autowired
//	AccountService accountService;
//	
//	@Autowired
//	StoreService storeService;
//	
//	@Autowired
//	PartnerService partnerService;
//	
//	@Autowired
//	EntityManager entityManager;
//
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Transactional
//	@Override
//	public Response<Transaction> saveSale(SaleDto saleDto) {
//		Response<Transaction> response = new Response<>();
//		Transaction transaction = new Transaction();
//		if(saleDto.getDueAmount()<=0)
//			throw new InvalidDataException("Invalid sale, 0 price");
//
//		if(saleDto.getDueAmount()>saleDto.getPaidAmount()+saleDto.getDiscount() && (saleDto.getClient()==null || saleDto.getClient().getId()==null || saleDto.getClient().getId().isEmpty() || !partnerRepo.findById(saleDto.getClient().getId()).isPresent())) 
//			throw new InvalidDataException("Credit sale requires customer details");
//
//		if(saleDto.getClient()!=null && saleDto.getClient().getId()!=null && !saleDto.getClient().getId().isEmpty()) {
//			Optional<Partner> oPartner = partnerRepo.findById(saleDto.getClient().getId());
//			if(oPartner.isPresent())
//				transaction.setPartner(oPartner.get());
//			else
//				transaction.setPartner(partnerService.getDefaultCustomer());
//		}else
//			transaction.setPartner(partnerService.getDefaultCustomer());
//		if(saleDto.getPaidAmount()>0)
//			if(saleDto.getPayMethod()!=null && saleDto.getPayMethod().getId()!=null && !saleDto.getPayMethod().getId().isEmpty()) {
//				Optional<Account> oAccount = accountRepo.findById(saleDto.getPayMethod().getId());
//				if(oAccount.isPresent())
//					transaction.setPaidAccount(oAccount.get());
//				else if(accountService.getDefaultPay()!=null)
//					transaction.setPaidAccount(accountService.getDefaultPay());
//				else
//					throw new InvalidDataException("Pay method not found");
//
//			}else
//				transaction.setPaidAccount(accountService.getDefaultPay());
//		
//		if(saleDto.getDiscount()>0) transaction.setDiscount(saleDto.getDiscount());
//		if(saleDto.getDueAmount()>0) transaction.setDueAmount(saleDto.getDueAmount());
//		if(saleDto.getPaidAmount()>0) transaction.setPaidAmount(saleDto.getPaidAmount());
//		if(saleDto.getNote()!=null) transaction.setDescription(saleDto.getNote());
//		transaction.setType(TransactionType.SALE);
//		transaction.setPayAccount(accountService.getSales());
//		transaction.setCreatedBy("Creator");
//		Transaction savedTransaction = transactionRepo.save(transaction);
//		List<TransactionDetail> details = saleDto.getDetails();
//		int total = 0;
//		if(details!=null) {
//			for(int i=0;i<details.size();i++) {
//				TransactionDetail detail = details.get(i);
//				if(detail.getProduct()!=null && detail.getProduct().getId()!=null) {
//					Optional<Product> oProduct = productRepo.findById(detail.getProduct().getId());
//					if(oProduct.isPresent()) {
//						Product product = oProduct.get();
//						if(product.getRemainingStock()<detail.getQuantity()) {
//							Product stored;
//							{
//								stored = storeService.pop(product);
//								if(stored!=null) {
//									int stock = stored.getRemainingStock()+product.getRemainingStock();
//									product.setRemainingStock(stock);
//									product.setExpires(stored.getExpires());
//								}else
//									break;
//							}while(storeService.exists(product) && product.getRemainingStock()<detail.getQuantity())
//							if(product.getRemainingStock()<detail.getQuantity()) {
//								TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//								throw new InvalidDataException(product.getName()+" out of stock");
//							}
//						}
//						detail.setTransaction(savedTransaction);
//						detail.setProduct(product);
//						detail.setPrice(detail.getQuantity()*product.getSellingPrice());
//						detail.setProfit(detail.getQuantity()*oProduct.get().getSellUnitProfit());
//						detail.setCreatedBy("Creator");
//						total+=detail.getPrice();
//						detailRepo.save(detail);
//						product.setRemainingStock(product.getRemainingStock()-detail.getQuantity());
//						if(product.getRemainingStock()==0) {
//							Product stored = storeService.pop(product);
//							if(stored!=null) {
//								product.setRemainingStock(stored.getRemainingStock());
//								product.setExpires(stored.getExpires());
//							}
//						}
//						productRepo.save(product);
//					}
//					else {
//						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//						throw new InvalidDataException("Product not available");
//					}
//				}
//			}
//		}
//		if(total!=savedTransaction.getDueAmount()) {
//			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//			throw new InvalidDataException("Due amout not valid");
//		}
//		Optional<Transaction> oTransaction = transactionRepo.findById(savedTransaction.getId());
//		if(oTransaction.isPresent()) {
//			response.setCode(ResponseCode.SUCCESS);
//			response.setData(oTransaction.get());
//			return response;
//		}
//		throw new InvalidDataException("Transaction could not be processed");
//	}
//	
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Transactional
//	@Override
//	public Response<Transaction> saveWholeSale(SaleDto saleDto) {
//		Response<Transaction> response = new Response<>();
//		Transaction transaction = new Transaction();
//		if(saleDto.getPaidAmount()<=0 && saleDto.getDueAmount()<=0) {
//			response.setCode(ResponseCode.INVALID_DATA);
//			response.setData(null);
//			response.setMessage("Invalid sale: Due amount is zero");
//			return response;
//		}
//		if(saleDto.getDueAmount()>(saleDto.getPaidAmount()+saleDto.getDiscount()) && (saleDto.getClient()==null || saleDto.getClient().getId()==null || saleDto.getClient().getId().isEmpty() || !partnerRepo.findById(saleDto.getClient().getId()).isPresent())) {
//			response.setCode(ResponseCode.INVALID_DATA);
//			response.setData(null);
//			response.setMessage("Credit sale requires customer details");
//			return response;
//		}
//		if(saleDto.getPaidAmount()>0)
//			if(saleDto.getPayMethod()!=null && saleDto.getPayMethod().getId()!=null && !saleDto.getPayMethod().getId().isEmpty()) {
//				Optional<Account> oAccount = accountRepo.findById(saleDto.getPayMethod().getId());
//				if(oAccount.isPresent())
//					transaction.setPaidAccount(oAccount.get());
//				else if(accountService.getDefaultPay()!=null)
//					transaction.setPaidAccount(accountService.getDefaultPay());
//				else {
//					response.setCode(ResponseCode.FAILURE);
//					response.setData(null);
//					response.setMessage("Pay method not found");
//					return response;
//				}
//			}else
//				transaction.setPaidAccount(accountService.getDefaultPay());
//		transaction.setPayAccount(accountService.getSales());
//		if(saleDto.getClient()!=null && saleDto.getClient().getId()!=null && !saleDto.getClient().getId().isEmpty()) {
//			Optional<Partner> oPartner = partnerRepo.findById(saleDto.getClient().getId());
//			if(oPartner.isPresent())
//				transaction.setPartner(oPartner.get());
//			else
//				transaction.setPartner(partnerService.getDefaultCustomer());
//		}else
//			transaction.setPartner(partnerService.getDefaultCustomer());
//		if(saleDto.getDiscount()>0) transaction.setDiscount(saleDto.getDiscount());
//		if(saleDto.getDueAmount()>0) transaction.setDueAmount(saleDto.getDueAmount());
//		if(saleDto.getPaidAmount()>0) transaction.setPaidAmount(saleDto.getPaidAmount());
//		if(saleDto.getNote()!=null) transaction.setDescription(saleDto.getNote());
//		transaction.setType(TransactionType.SALE);
//		transaction.setCreatedBy("Creator");
//		Transaction savedTransaction = transactionRepo.save(transaction);
//		List<TransactionDetail> details = saleDto.getDetails();
//		int total = 0;
//		if(details!=null) {
//			for(int i=0;i<details.size();i++) {
//				TransactionDetail detail = details.get(i);
//				if(detail.getProduct()!=null && detail.getProduct().getId()!=null) {
//					Optional<Product> oProduct = productRepo.findById(detail.getProduct().getId());
//					if(oProduct.isPresent()) {
//						Product product = oProduct.get();
//						if(product.getRemainingStock()<detail.getQuantity()) {
//							Product stored;
//							while(storeService.exists(product) && product.getRemainingStock()<detail.getQuantity()*product.getSellingPerBuying()) {
//								stored = storeService.pop(product);
//								product.setRemainingStock(product.getRemainingStock()+stored.getRemainingStock());
//								product.setExpires(stored.getExpires());
//							}
//							if(product.getRemainingStock()<detail.getQuantity()*product.getSellingPerBuying()) {
//								TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//								throw new InvalidDataException(product.getName()+" out of stock");
//							}
//						}
//						product.setRemainingStock(product.getRemainingStock()-(detail.getQuantity()*product.getSellingPerBuying()));
//						detail.setTransaction(savedTransaction);
//						detail.setProduct(product);
//						detail.setPrice(detail.getQuantity()*product.getWholeSalePrice());
//						detail.setProfit(detail.getQuantity()*(product.getWholeSalePrice()- product.getBuyingPrice()));
//						detail.setCreatedBy("Creator");
//						detail.setQuantity(detail.getQuantity()*product.getSellingPerBuying());
//						total+=detail.getPrice();
//						detailRepo.save(detail);
//						productRepo.save(product);
//					}
//					else {
//						//detailRepo.deleteByTransaction(savedTransaction);
//						//transactionRepo.deleteById(savedTransaction.getId());
//						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//						response.setCode(ResponseCode.INVALID_DATA);
//						response.setData(null);
//						response.setMessage("Product not available");
//						return response;
//					}
//				}
//			}
//		}
//		if(total!=savedTransaction.getDueAmount()) {
//			//detailRepo.deleteByTransactionId(savedTransaction.getId());
//			//transactionRepo.deleteById(savedTransaction.getId());
//			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//			response.setCode(ResponseCode.INVALID_DATA);
//			response.setData(null);
//			response.setMessage("Due amount not valid ");
//			return response;
//		}
//		Optional<Transaction> oTransaction = transactionRepo.findById(savedTransaction.getId());
//		if(oTransaction.isPresent()) {
//			response.setCode(ResponseCode.SUCCESS);
//			response.setData(oTransaction.get());
//			return response;
//		}
//		response.setCode(ResponseCode.FAILURE);
//		response.setData(null);
//		response.setMessage("Transaction could not be processed");
//		return response;
//	}
//	
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Transactional
//	@Override
//	public Response<PartnerDto> clearCreditPurchase(SaleDto saleDto) {
//		Response<PartnerDto> response = new Response<>();
//		if(saleDto.getPaidAmount()<=0) 
//			throw new InvalidDataException("Paid amount is not valid");
//		if(saleDto.getClient()!=null && saleDto.getClient().getId()!=null && !saleDto.getClient().getId().isEmpty()) {
//			Optional<Partner> oClient = partnerRepo.findById(saleDto.getClient().getId());
//			if(oClient.isPresent()) {
//				Transaction transaction = new Transaction();
//				if(saleDto.getPayMethod()!=null && saleDto.getPayMethod().getId()!=null && !saleDto.getPayMethod().getId().isEmpty()) {
//					Optional<Account> oAccount = accountRepo.findById(saleDto.getPayMethod().getId());
//					if(oAccount.isPresent())
//						transaction.setPayAccount(oAccount.get());
//					else if(accountService.getDefaultPay()!=null)
//						transaction.setPayAccount(accountService.getDefaultPay());
//					else 
//						throw new InvalidDataException("Pay method not found");
//				}else
//					transaction.setPayAccount(accountService.getDefaultPay());
//				transaction.setPaidAccount(accountService.getPurchase());
//				transaction.setPaidAmount(saleDto.getPaidAmount());
//				transaction.setPartner(oClient.get());
//				transaction.setCreatedBy("Creator");
//				transaction.setType(TransactionType.PURCHASE);
//				transactionRepo.save(transaction);
//				response.setCode(ResponseCode.SUCCESS);
//				PartnerDto partner = new PartnerDto();
//				BeanUtils.copyProperties(oClient.get(), partner);
//				int debt = transactionRepo.findDebtByPartnerId(oClient.get().getId());
//				if(debt>0)
//					partner.setDebt(debt);
//				else
//					partner.setBalance(Math.abs(debt));
//				response.setData(partner);
//				response.setMessage("");
//				return response;
//			}
//		}
//		throw new InvalidDataException("No valid client provided");
//	}
//	
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Transactional
//	@Override
//	public Response<PartnerDto> clearCreditSale(SaleDto saleDto) {
//		Response<PartnerDto> response = new Response<>();
//		if(saleDto.getPaidAmount()<=0) 
//			throw new InvalidDataException("Paid amount is not valid");
//		if(saleDto.getClient()!=null && saleDto.getClient().getId()!=null && !saleDto.getClient().getId().isEmpty()) {
//			Optional<Partner> oClient = partnerRepo.findById(saleDto.getClient().getId());
//			if(oClient.isPresent()) {
//				Transaction transaction = new Transaction();
//				if(saleDto.getPayMethod()!=null && saleDto.getPayMethod().getId()!=null && !saleDto.getPayMethod().getId().isEmpty()) {
//					Optional<Account> oAccount = accountRepo.findById(saleDto.getPayMethod().getId());
//					if(oAccount.isPresent())
//						transaction.setPaidAccount(oAccount.get());
//					else if(accountService.getDefaultPay()!=null)
//						transaction.setPaidAccount(accountService.getDefaultPay());
//					else 
//						throw new InvalidDataException("Pay method not found");
//				}else
//					transaction.setPaidAccount(accountService.getDefaultPay());
//				transaction.setPayAccount(accountService.getSales());
//				transaction.setPaidAmount(saleDto.getPaidAmount());
//				transaction.setPartner(oClient.get());
//				transaction.setCreatedBy("Creator");
//				transaction.setType(TransactionType.SALE);
//				transactionRepo.save(transaction);
//				response.setCode(ResponseCode.SUCCESS);
//				PartnerDto partner = new PartnerDto();
//				BeanUtils.copyProperties(oClient.get(), partner);
//				int debt = transactionRepo.findDebtByPartnerId(oClient.get().getId());
//				if(debt>0)
//					partner.setDebt(debt);
//				else
//					partner.setBalance(Math.abs(debt));
//				response.setData(partner);
//				response.setMessage("");
//				return response;
//			}
//		}
//		throw new InvalidDataException("No valid client provided");
//	}
//
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Transactional
//	@Override
//	public Response<Transaction> savePurchase(SaleDto purchaseDto) {
//		Response<Transaction> response = new Response<>();
//		Transaction transaction = new Transaction();
//		if(purchaseDto.getDueAmount()<=0) 
//			throw new InvalidDataException("Invalid purchase, 0 price");
//
//		if(purchaseDto.getDueAmount()>purchaseDto.getPaidAmount() && (purchaseDto.getClient()==null || purchaseDto.getClient().getId()==null || purchaseDto.getClient().getId().isEmpty() || !partnerRepo.findById(purchaseDto.getClient().getId()).isPresent()))
//			throw new InvalidDataException("Credit purchase requires supplier details");
//
//		if(purchaseDto.getClient()!=null && purchaseDto.getClient().getId()!=null && !purchaseDto.getClient().getId().isEmpty()) {
//			Optional<Partner> oPartner = partnerRepo.findById(purchaseDto.getClient().getId());
//			if(oPartner.isPresent())
//				transaction.setPartner(oPartner.get());
//			else
//				transaction.setPartner(partnerService.getDefaultSupplier());
//		}else
//			transaction.setPartner(partnerService.getDefaultSupplier());
//		if(purchaseDto.getReceipt()!=null) transaction.setReceipt(purchaseDto.getReceipt());
//		if(purchaseDto.getDiscount()>0) transaction.setDiscount(purchaseDto.getDiscount());
//		if(purchaseDto.getDueAmount()>0) transaction.setDueAmount(purchaseDto.getDueAmount());
//		if(purchaseDto.getPaidAmount()>0) transaction.setPaidAmount(purchaseDto.getPaidAmount());
//		if(purchaseDto.getNote()!=null) transaction.setDescription(purchaseDto.getNote());
//		transaction.setType(TransactionType.PURCHASE);
//		if(purchaseDto.getPayMethod()!=null && purchaseDto.getPayMethod().getId()!=null && !purchaseDto.getPayMethod().getId().isEmpty()) {
//			Optional<Account> oAccount = accountRepo.findById(purchaseDto.getPayMethod().getId());
//			if(oAccount.isPresent())
//				transaction.setPayAccount(oAccount.get());
//			else if(accountService.getDefaultPay()!=null)
//				transaction.setPayAccount(accountService.getDefaultPay());
//			else 
//				throw new InvalidDataException("Pay method not found");
//		}else
//			transaction.setPayAccount(accountService.getDefaultPay());
//		transaction.setPaidAccount(accountService.getPurchase());
//		transaction.setCreatedBy("Creator");
//		Transaction savedTransaction = transactionRepo.save(transaction);
//		List<TransactionDetail> details = purchaseDto.getDetails();
//		int total = 0;
//		if(details!=null) {
//			for(int i=0;i<details.size();i++) {
//				TransactionDetail detail = details.get(i);
//				if(detail.getProduct()!=null && detail.getProduct().getId()!=null) {
//					Optional<Product> oProduct = productRepo.findById(detail.getProduct().getId());
//					if(oProduct.isPresent()) {
//						Product product = oProduct.get();
//						detail.setTransaction(savedTransaction);
//						detail.setProduct(product);
//						if(product.isPerishable()) {
//							if(detail.getExpires()==null) {
//								TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//								throw new InvalidDataException("No expire date provided for "+product.getName());
//							}
//							if(product.getRemainingStock()>0 && product.getExpires()!=detail.getExpires())
//								if(product.getExpires()!=null && product.getExpires().isAfter(detail.getExpires())) {
//									TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//									throw new InvalidDataException(product.getName()+ " expires before your current stock");
//								}else
//									storeService.push(product, detail.getQuantity()*product.getSellingPerBuying(), detail.getExpires());
//							else {
//								product.setRemainingStock(product.getRemainingStock()+(detail.getQuantity()*product.getSellingPerBuying()));
//								product.setExpires(detail.getExpires());
//							}
//						}
//						if(detail.getPrice()!=detail.getQuantity()*product.getBuyingPrice())
//							product.setBuyingPrice(detail.getPrice()/detail.getQuantity());
//						if(detail.getSellingPrice()>0 && detail.getSellingPrice()!=product.getSellingPrice())
//							product.setSellingPrice(detail.getSellingPrice());
//						if(detail.getWholeSalePrice()>0 && detail.getWholeSalePrice()!=product.getWholeSalePrice())
//							product.setWholeSalePrice(detail.getWholeSalePrice());
//						if(product.getSellingPerBuying()>0)
//							product.setSellUnitProfit(((product.getSellingPrice()*product.getSellingPerBuying()) - product.getBuyingPrice())/product.getSellingPerBuying());
//						detail.setProfit(detail.getQuantity()*(product.getWholeSalePrice()- product.getBuyingPrice()));
//						detail.setCreatedBy("Creator");
//						detail.setQuantity(detail.getQuantity()*product.getSellingPerBuying());
//						total+=detail.getPrice();
//						if(product.getSellUnitProfit()<=0 || (product.getWholeSalePrice()>0 && product.getWholeSalePrice()<=product.getBuyingPrice())) {
//							TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//							throw new InvalidDataException("Invalid sale prices for "+product.getName());
//						}
//						detailRepo.save(detail);
//						productRepo.save(product);
//					}
//					else {
//						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//						throw new InvalidDataException("Product not available");
//					}
//				}
//			}
//		}
//		if(total!=savedTransaction.getDueAmount()) {
//			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//			throw new InvalidDataException("Due amount not valie");
//		}
//		Optional<Transaction> oTransaction = transactionRepo.findById(savedTransaction.getId());
//		if(oTransaction.isPresent()) {
//			response.setCode(ResponseCode.SUCCESS);
//			response.setData(oTransaction.get());
//			return response;
//		}
//		throw new InvalidDataException("Transaction could not be processed");
//	}
//
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Transactional
//	@Override
//	public Response<Transaction> saveExpense(TransactionDto transactionDto) {
//		Response<Transaction> response = new Response<>();
//		if(transactionDto.getPaidAmount()<=0)
//			throw new InvalidDataException("Paid amount not valid");
//
//		Transaction transaction = new Transaction();
//		if(transactionDto.getPayAccount()!=null && transactionDto.getPayAccount().getId()!=null && !transactionDto.getPayAccount().getId().isEmpty()) {
//			Optional<Account> oAccount = accountRepo.findById(transactionDto.getPayAccount().getId());
//			if(oAccount.isPresent())
//				transaction.setPayAccount(oAccount.get());
//			else if(accountService.getDefaultPay()!=null)
//				transaction.setPayAccount(accountService.getDefaultPay());
//			else 
//				throw new InvalidDataException("Pay method not found");
//		}else
//			transaction.setPayAccount(accountService.getDefaultPay());
//		if(transactionDto.getPaidAccount()!=null && transactionDto.getPaidAccount().getId()!=null && !transactionDto.getPaidAccount().getId().isEmpty()) {
//			Optional<Account> oAccount = accountRepo.findById(transactionDto.getPaidAccount().getId());
//			if(oAccount.isPresent())
//				transaction.setPaidAccount(oAccount.get());
//			else 
//				throw new InvalidDataException("Expense account not found");
//		}else
//			throw new InvalidDataException("Expense account not found");
//		transaction.setPaidAmount(transactionDto.getPaidAmount());
//		transaction.setCreatedBy("Creator");
//		transaction.setType(TransactionType.EXPENSE);
//		Transaction savedTransaction = transactionRepo.save(transaction);
//		response.setCode(ResponseCode.SUCCESS);
//		response.setData(savedTransaction);
//		response.setMessage("");
//		return response;
//	}
//	
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Override
//	public Response<List<Transaction>> getTransactions() {
//		Response<List<Transaction>> response = new Response<>();
//		List<Transaction> tx_transactions = transactionRepo.getAll();
//		response.setData(tx_transactions);
//		response.setCode(ResponseCode.SUCCESS);
//		response.setMessage("");
//		return response;
//	}
//
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Override
//	public Response<SalesReportDto> getSales(LocalDate startDate, LocalDate endDate) {
//		Response<SalesReportDto> response = new Response<>();
//		SalesReportDto salesReportDto = new SalesReportDto();
//		salesReportDto.setProducts(this.getSaleByProduct(startDate, endDate));
//		salesReportDto.setClients(this.getSaleByClient(startDate, endDate));
//		salesReportDto.setDates(this.getSaleByDate(startDate, endDate));
//		
//		response.setData(salesReportDto);
//		response.setCode(ResponseCode.SUCCESS);
//		response.setMessage("");
//		return response;
//	}
//
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Override
//	public Response<List<Transaction>> getPurchases() {
//		Response<List<Transaction>> response = new Response<>();
//		List<Transaction> tx_transactions = transactionRepo.getAllByType(TransactionType.PURCHASE.ordinal());
//		response.setData(tx_transactions);
//		response.setCode(ResponseCode.SUCCESS);
//		response.setMessage("");
//		return response;
//	}
//
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Override
//	public Response<List<Transaction>> getExpenses() {
//		Response<List<Transaction>> response = new Response<>();
//		List<Transaction> tx_transactions = transactionRepo.getAllByType(TransactionType.EXPENSE.ordinal());
//		response.setData(tx_transactions);
//		response.setCode(ResponseCode.SUCCESS);
//		response.setMessage("");
//		return response;
//	}
//	
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Override
//	public Response<List<TrialBalanceDto>> getTrialBalance(LocalDate endDate) {
//		Response<List<TrialBalanceDto>> response = new Response<>();
//		List<TrialBalanceDto> trials = this.getReportTrialBalance(endDate);
//		response.setData(trials);
//		response.setCode(ResponseCode.SUCCESS);
//		response.setMessage("");
//		return response;
//	}
//	
//	public List<SaleProductDto> getSaleByProduct(LocalDate startDate, LocalDate endDate){
//		List<SaleProductDto> sales = entityManager.createNativeQuery("select name as product, sum(quantity) as quantity, sum(price) as gross from tx_transactions inner join tx_transaction_details on tx_transactions.id=tx_transaction_details.transaction inner join tx_products on tx_products.id=tx_transaction_details.product where type=0 and cast(tx_transactions.created_at as date)>=:startDate and cast(tx_transactions.created_at as date)<=:endDate group by name order by gross desc")
//				.setParameter("startDate",startDate).setParameter("endDate", endDate)
//				.unwrap(org.hibernate.query.NativeQuery.class)
//				.setResultTransformer(Transformers.aliasToBean(SaleProductDto.class))
//				.getResultList();
//		return sales;
//	}
//	
//	public List<SaleDateDto> getSaleByDate(LocalDate startDate, LocalDate endDate){
//		List<SaleDateDto> sales = entityManager.createNativeQuery("select cast(created_at as date)as date, sum(due_amount) as gross, sum(paid_amount) as paid, sum(discount) as discount from tx_transactions  where type=0 and cast(created_at as date)>=:startDate and cast(created_at as date)<=:endDate group by cast(created_at as date) order by cast(created_at as date) asc")
//				.setParameter("startDate",startDate).setParameter("endDate", endDate)
//				.unwrap(org.hibernate.query.NativeQuery.class)
//				.setResultTransformer(Transformers.aliasToBean(SaleDateDto.class))
//				.getResultList();
//		return sales;
//	}
//	
//	public List<SaleClientDto> getSaleByClient(LocalDate startDate, LocalDate endDate){
//		List<SaleClientDto> sales = entityManager.createNativeQuery("select max(name) as name, max(phone) as phone, max(email)as email, sum(due_amount) as gross, sum(paid_amount) as paid, sum(due_amount-paid_amount-discount) as debt, sum(discount) as discount from tx_transactions inner join tx_partners on tx_partners.id=tx_transactions.partner where type=0  and cast(tx_transactions.created_at as date)>=:startDate and cast(tx_transactions.created_at as date)<=:endDate group by partner order by name asc")
//				.setParameter("startDate",startDate).setParameter("endDate", endDate)
//				.unwrap(org.hibernate.query.NativeQuery.class)
//				.setResultTransformer(Transformers.aliasToBean(SaleClientDto.class))
//				.getResultList();
//		return sales;
//	}
//	
//	public List<TrialBalanceDto> getReportTrialBalance(LocalDate endDate){
//		return entityManager.createNativeQuery(
//					"select name, case when sum(debit)>sum(credit) then sum(debit)-sum(credit) else 0 end as debit, case when sum(debit)<sum(credit) then sum(credit)-sum(debit) else 0 end as credit from ("+
//						"select name, sum(paid_amount) as debit, 0 as credit from tx_transactions inner join tx_accounts on tx_transactions.paid_account=tx_accounts.id where type=0 and cast(tx_transactions.created_at as date)<=? group by name"+
//						" union "+
//						"select 'Discount' as name, sum(discount) as debit, 0 as credit from tx_transactions where type=0 and cast(tx_transactions.created_at as date)<=? group by name"+
//						" union "+
//						"select name, sum(due_amount) as debit, 0 as credit from tx_transactions inner join tx_accounts on tx_transactions.paid_account=tx_accounts.id where (type=1 or type=2) and cast(tx_transactions.created_at as date)<=? group by name"+
//						" union "+
//						"select 'Receivable' as name, sum(due_amount) - sum(paid_amount) - sum(discount) as debit, 0 as credit from tx_transactions where type=0 and cast(tx_transactions.created_at as date)<=? group by name"+
//						" union "+
//						"select name, 0 as debit, sum(paid_amount) as credit from tx_transactions inner join tx_accounts on tx_transactions.pay_account=tx_accounts.id where (type=1 or type=2) and cast(tx_transactions.created_at as date)<=? group by name"+
//						" union "+
//						"select name, 0 as debit, sum(due_amount) as credit from tx_transactions inner join tx_accounts on tx_transactions.pay_account=tx_accounts.id where type=0 and cast(tx_transactions.created_at as date)<=? group by name"+
//						" union "+
//						"select 'Payable' as name, 0 as debit, sum(due_amount) - sum(paid_amount) - sum(discount) as credit from tx_transactions where (type=1 or type=2) and cast(tx_transactions.created_at as date)<=? group by name"+
//					") as a group by name order by name asc	")
//		.setParameter(1, endDate).setParameter(2, endDate).setParameter(3, endDate).setParameter(4, endDate).setParameter(5, endDate).setParameter(6, endDate).setParameter(7, endDate)
//		.unwrap(org.hibernate.query.NativeQuery.class)
//		.setResultTransformer(Transformers.aliasToBean(TrialBalanceDto.class))
//		.getResultList();
//	}
//
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SELLER')")
//	@Override
//	public Response<List<StockMoveReportDto>> getStockMovement(LocalDate startDate, LocalDate endDate) {
//		Response<List<StockMoveReportDto>> response = new Response<List<StockMoveReportDto>>();
//		List<StockIssueDto> stocks = entityManager.createNativeQuery("select cast(tx_transactions.created_at as date)as date, name as product, quantity, price, profit,type from tx_transactions inner join tx_transaction_details on tx_transactions.id=tx_transaction_details.transaction inner join tx_products on tx_products.id=tx_transaction_details.product where cast(tx_transactions.created_at as date)>=:startDate and cast(tx_transactions.created_at as date)<=:endDate and tx_transactions.deleted=false order by date desc, name asc")
//				.setParameter("startDate",startDate).setParameter("endDate", endDate)
//				.unwrap(org.hibernate.query.NativeQuery.class)
//				.setResultTransformer(Transformers.aliasToBean(StockIssueDto.class))
//				.getResultList();
//		String receiptName = null, issueName = null;
//		Date date = null;
//		int recQty = 0, issQty = 0, recPrice = 0, issPrice = 0, recProfit = 0, issProfit = 0, i=0, size = stocks.size();
//		List<StockMoveReportDto> result = new ArrayList<StockMoveReportDto>();
//		List<StockReceiptDto> receipts = new ArrayList<StockReceiptDto>();
//		List<StockIssueDto> issues = new ArrayList<StockIssueDto>();
//		StockIssueDto issue = new StockIssueDto();
//		StockReceiptDto receipt = new StockReceiptDto();
//		do{
//			if(i==size || (date!=null && !stocks.get(i).getDate().equals(date))) {
//				if(issue.getQuantity()>0)
//					issues.add(issue);
//				if(receipt.getQuantity()>0)
//					receipts.add(receipt);
//				StockMoveReportDto obj = new StockMoveReportDto(date,receipts,issues);
//				result.add(obj);
//				issue = new StockIssueDto();
//				receipt = new StockReceiptDto();
//				receipts = new ArrayList<StockReceiptDto>();
//				issues = new ArrayList<StockIssueDto>();
//				if(i<size)
//					date = stocks.get(i).getDate();
//			}else if(!stocks.get(i).getDate().equals(date)) {
//				date = stocks.get(i).getDate();
//			}
//			if(i==size)
//				break;
//			if(stocks.get(i).getType()==0) {
//				if(issue.getProduct()==null || !stocks.get(i).getProduct().contentEquals(issue.getProduct())) {
//					if(issue.getProduct()!=null) {
//						issues.add(issue);
//					}
//					issue = new StockIssueDto(stocks.get(i).getDate(),stocks.get(i).getProduct(),stocks.get(i).getQuantity(),stocks.get(i).getPrice(),stocks.get(i).getProfit(),stocks.get(i).getType());
//				}else {
//					issue.add(stocks.get(i).getQuantity(), stocks.get(i).getPrice(), stocks.get(i).getProfit());
//				}
//
//			}else if(stocks.get(i).getType()==1) {
//				if(receipt.getProduct()==null || !stocks.get(i).getProduct().contentEquals(receipt.getProduct())) {
//					if(receipt.getProduct()!=null) {
//						receipts.add(receipt);
//					}
//					receipt = new StockReceiptDto(stocks.get(i).getDate(),stocks.get(i).getProduct(),stocks.get(i).getQuantity(),stocks.get(i).getPrice(),stocks.get(i).getType());
//				}else{
//					receipt.add(stocks.get(i).getQuantity(), stocks.get(i).getPrice());
//				}
//			}
//			i++;
//		}while(i<=size);
//		
//		response.setData(result);
//		response.setCode(ResponseCode.SUCCESS);
//		return response;
//	}
//
//}
