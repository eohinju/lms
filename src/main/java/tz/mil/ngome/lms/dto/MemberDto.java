package tz.mil.ngome.lms.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.utils.Configuration;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

	private String id;
	private int compNumber;
	private String serviceNumber;
	private String rank;
	private String firstName;
	private String middleName;
	private String lastName;
	private String phone;
	private String unit;
	private String subUnit = "HL";
	private String payAccount;
	private String payBank;
	private LocalDate dob;
	private LocalDate rod;

	public MemberDto(int comp, String svc, String rank, String fname, String mname, String lname, String unit) {
		this.compNumber = comp;
		this.serviceNumber = svc;
		this.rank = rank;
		this.firstName = fname;
		this.middleName = mname;
		this.lastName = lname;
		this.unit = unit;
	}

	public MemberDto(int comp, String svc, String rank, String fname, String mname, String lname, String subUnit, String phone) {
		Configuration conf = new Configuration();
		this.compNumber = comp;
		this.serviceNumber = svc;
		this.rank = rank;
		this.firstName = fname;
		this.middleName = mname;
		this.lastName = lname;
		this.subUnit = subUnit;
		this.phone = phone;
		this.unit = conf.getUnit();
	}

}
