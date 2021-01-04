package tz.mil.ngome.lms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseEntity {
	
	@Column(nullable = false, unique = true, length = 32)
	private String username;
	
	@JsonIgnore
	private String password;
	
	private Role role;
	
	public enum Role{
		ROLE_ADMIN, ROLE_CHAIRMAN, ROLE_ACCOUNTANT, ROLE_LEADER, ROLE_CLERK, ROLE_MEMBER
	}
	
}
