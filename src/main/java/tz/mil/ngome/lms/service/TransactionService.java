package tz.mil.ngome.lms.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.dto.TransactionDto;
import tz.mil.ngome.lms.entity.Account;
import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.utils.Response;

@Service
public interface TransactionService {

	public Response<TransactionDto> journalLoan(Loan loan, Account payAccount, LocalDate date);
//	public Response<Transaction> saveSale(SaleDto saleDto);
//	public Response<Transaction> saveWholeSale(SaleDto saleDto);
//	public Response<Transaction> savePurchase(SaleDto purchaseDto);
//	public Response<Transaction> saveExpense(TransactionDto transactionDto);
//	public Response<PartnerDto> clearCreditSale(SaleDto purchaseDto);
//	public Response<PartnerDto> clearCreditPurchase(SaleDto purchaseDto);
//	public Response<List<Transaction>> getTransactions();
//	public Response<SalesReportDto> getSales(LocalDate startDate, LocalDate endDate);
//	public Response<List<Transaction>> getPurchases();
//	public Response<List<Transaction>> getExpenses();
//	public Response<List<TrialBalanceDto>> getTrialBalance(LocalDate endDate);
//	public Response<List<StockMoveReportDto>> getStockMovement(LocalDate startDate, LocalDate endDate);

}
