package tz.mil.ngome.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import tz.mil.ngome.lms.dto.TransactionDetailDto;
import tz.mil.ngome.lms.entity.Transaction;
import tz.mil.ngome.lms.entity.TransactionDetail;

public interface TransactionDetailRepository extends JpaRepository<TransactionDetail, String> {

	void deleteByTransaction(Transaction savedTransaction);

	@Transactional
	@Modifying
	@Query(value = "delete from transactiondetail where transaction=:id", nativeQuery = true)
	void deleteByTransactionId(String id);

	@Query("SELECT new tz.mil.ngome.lms.dto.TransactionDetailDto("
			+ "detail.id, detail.account.id, detail.debit, detail.credit)"
			+ "FROM TransactionDetail AS detail where detail.transaction.id=:id order by detail.debit asc")
	List<TransactionDetailDto> findDetailsByTransactionId(String id);

	@Query(value = "select sum(debit) as debit from transactiondetail where account=:id", nativeQuery = true)
	Integer getDebitByAccount(String id);

	@Query(value = "select sum(credit) as credit from transactiondetail where account=:id", nativeQuery = true)
	Integer getCreditByAccount(String id);

}
