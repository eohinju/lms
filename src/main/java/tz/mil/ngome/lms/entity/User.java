package tz.mil.ngome.lms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "users")
public class User extends BaseEntity {
	
	@Column(nullable = false, unique = true, length = 32)
	private String username;
	
	@Column(length = 32)
	private String email;
	
	@JsonIgnore
	private String password;
	
	@Column
	private String name;
	
	@Column
	private Role role;
	
	public enum Role{
		ROLE_ADMIN, ROLE_CHAIRMAN, ROLE_ACCOUNTANT, ROLE_LEADER, ROLE_CLERK, ROLE_MEMBER
	}
	
}
