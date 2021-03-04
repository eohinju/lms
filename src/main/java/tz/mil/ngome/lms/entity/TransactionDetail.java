package  tz.mil.ngome.lms.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Table(name = "transaction_details")
public class TransactionDetail extends BaseEntity {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction", referencedColumnName = "id")
	@JsonBackReference
	private Transaction transaction;
	
	@Basic(optional = false)
	@ManyToOne
	@JoinColumn(name = "account", referencedColumnName = "id", nullable = false)
	private Account account;

	@Basic(optional = false)
	@Column(name = "debit", nullable = false)
	private double debit = 0;

	@Basic(optional = false)
	@Column(name = "credit", nullable = false)
	private double credit = 0;
	
}
