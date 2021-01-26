//package tz.co.emmapho.duka.service;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//
//import com.github.openjson.JSONArray;
//import com.github.openjson.JSONObject;
//
//import tz.co.emmapho.duka.dto.PartnerDto;
//import tz.co.emmapho.duka.dto.SaleDto;
//import tz.co.emmapho.duka.dto.SalesReportDto;
//import tz.co.emmapho.duka.dto.StockMoveReportDto;
//import tz.co.emmapho.duka.dto.TransactionDto;
//import tz.co.emmapho.duka.dto.TrialBalanceDto;
//import tz.co.emmapho.duka.entity.Transaction;
//import tz.co.emmapho.duka.utils.Response;
//
//@Service
//public interface TransactionService {
//
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
//
//}
