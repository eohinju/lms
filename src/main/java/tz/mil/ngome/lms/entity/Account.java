package  tz.mil.ngome.lms.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class Account extends BaseEntity {

	@Basic(optional = false)
	@Column(name = "name", length = 64, nullable = false, unique = true)
	private String name;
	
	@Basic(optional = false)
	@ManyToOne
	@JoinColumn(name = "account_type", referencedColumnName = "id", nullable = false)
	private AccountType accountType;
	
}
