package tz.mil.ngome.lms.entity;

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
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Contribution extends BaseEntity {
	
	@ManyToOne
	@JoinColumn(nullable = false,referencedColumnName = "id")
	private Member member;
	
	@Column(nullable = false)
	private int amount;
	
	@Column(nullable = false, length = 16)
	private String month;

}
