package tz.mil.ngome.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import tz.mil.ngome.lms.entity.Transaction;
import tz.mil.ngome.lms.entity.TransactionDetail;

public interface TransactionDetailRepo extends JpaRepository<TransactionDetail, String> {

	void deleteByTransaction(Transaction savedTransaction);

	@Transactional
	@Modifying
	@Query(value = "delete from transactiondetail where transaction=:id", nativeQuery = true)
	void deleteByTransactionId(String id);

}
