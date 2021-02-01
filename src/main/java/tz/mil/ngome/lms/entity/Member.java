package tz.mil.ngome.lms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.utils.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "members")
public class Member extends BaseEntity {
	
	@Column(unique = true, nullable = false)
	private int compNumber;
	
	@Column(unique = true, nullable = false, length = 16)
	private String serviceNumber;
	
	@Column(nullable = false)
	private String rank;
	
	@Column(nullable = false, length = 16)
	private String firstName;
	
	@Column(nullable = false, length = 16)
	private String middleName;
	
	@Column(nullable = false, length = 16)
	private String lastName;
	
	@Column(length = 16)
	private String phone;
	
	@Column(nullable = false, length = 8)
	private String unit;
	
	@Column(nullable = false, length = 16)
	private String subUnit;
	
	public String getName() {
		return this.serviceNumber.trim()+" "+this.getRank().trim()+" "+this.firstName.trim()+" "+this.middleName.trim()+" "+this.lastName.trim();
	}
}
