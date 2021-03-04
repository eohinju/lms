package  tz.mil.ngome.lms.entity;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.utils.BaseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

	@Basic(optional = false)
	@Column(name = "date", nullable = false)
	private LocalDate date;
	
	@Basic(optional = true)
	@Column(name = "receipt", length = 128, nullable = true)
	private String receipt;

	@Basic(optional = true)
	@Column(name = "description", length = 128, nullable = true)
	private String description;

	@Basic(optional = false)
	@Column(name = "debit")
	private double debit = 0;

	@Basic(optional = false)
	@Column(name = "credit")
	private double credit = 0;

	@OneToMany(mappedBy = "transaction")
	@JsonManagedReference
	private Set<TransactionDetail> details;
	
}
