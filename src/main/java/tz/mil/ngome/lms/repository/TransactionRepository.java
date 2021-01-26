package tz.mil.ngome.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tz.mil.ngome.lms.dto.TransactionDto;
import tz.mil.ngome.lms.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

	@Query(value = "select sum(due_amount)-sum(paid_amount) from tx_transactions where deleted=false and partner=:id", nativeQuery = true)
	Integer findDebtByPartnerId(String id);

	@Query(value = "select * from tx_transactions where deleted=false order by created_at desc", nativeQuery = true)
	List<Transaction> getAll();

	@Query(value = "select * from tx_transactions where type=:type and deleted=false order by created_at desc", nativeQuery = true)
	List<Transaction> getAllByType(int type);
	
	@Query("SELECT new tz.mil.ngome.lms.dto.TransactionDto("
			+ "txn.id, txn.date, txn.receipt, txn.description)"
			+ "FROM Transaction AS txn WHERE txn.id=:id")
	TransactionDto findTransactionById(String id);

//	@Query("select new webapi.campaign.dto.StatsDTO(count(u.objective_type_id),u.objective_type_id,u.modified_at) "
//	        + "from user_campaign_objective u where u.campaign_id = ?1 group by u.objective_type_id,u.modified_at")
//	@Query("select new tz.co.emmapho.duka.dto.SaleProductDto(u.name,sum(u.quantity),sum(u.price))"
//	+"from (transactions inner join transaction_details on transactions.id=transaction_details.transaction inner join products on products.id=transaction_details.product) u where u.type=0 and transactions.created_at>=:startDate and transactions.created_at<=:endDate group by u.name order by u.name asc")	
//	@Query(value = "select name as product, sum(quantity) as quantity, sum(price) as gross from transactions inner join transaction_details on transactions.id=transaction_details.transaction inner join products on products.id=transaction_details.product where type=0 and transactions.created_at>=:startDate and transactions.created_at<=:endDate group by name order by name asc", name = "getSaleByProduct")
//	List<SaleProductDto> getSaleByProduct(LocalDate startDate, LocalDate endDate);
//	
//	@Query(value = "select max(name) as name, max(phone) as phone, max(email)as email, sum(due_amount) as gross, sum(paid_amount) as paid, sum(due_amount-paid_amount) as debt, sum(discount) as discount from transactions inner join partners on partners.id=transactions.partner where type=0  and transactions.created_at>=:startDate and transactions.created_at<=:endDate group by partner order by name asc", nativeQuery = true)
//	List<SaleClientDto> getSaleByClient(LocalDate startDate, LocalDate endDate);
//	
//	@Query(value = "select cast(created_at as date)as date, sum(due_amount) as gross, sum(paid_amount) as paid, sum(discount) as discount from transactions  where type=0 and created_at>=:startDate and created_at<=:endDate group by cast(created_at as date) order by cast(created_at as date) asc", nativeQuery = true)
//	List<SaleDateDto> getSaleByDate(LocalDate startDate, LocalDate endDate);

}
