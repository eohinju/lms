package tz.mil.ngome.lms.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.repository.TransactionDetailRepository;
import tz.mil.ngome.lms.utils.SpringContext;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

	@JsonIgnore
	private TransactionDetailRepository transactionDetailRepo = SpringContext.getBean(TransactionDetailRepository.class);
	
	private String id;
	private LocalDate date;
	private String receipt;
	private String description;
	private List<TransactionDetailDto> details;
	
	public TransactionDto(String id, LocalDate date, String receipt, String description) {
		this.id = id;
		this.date = date;
		this.receipt = receipt;
		this.description = description;
		this.details = transactionDetailRepo.findDetailsByTransactionId(id);
	}
}
