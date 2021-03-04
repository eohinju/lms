package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeductionsRequiredDto {

	private String loan;
	private double amount;
	private double balance;
	private boolean checked = false;
	
	public DeductionsRequiredDto(String loan, double amount, double balance) {
		this.loan = loan;
		this.amount = amount;
		this.balance = balance;
	}

	public String toString() {

		return new StringBuilder().append("DeductionsRequiredDto{\n").append("\tloan: "+loan+",\n").append("\tamount: ")
				.append(amount).append("\n\tbalance: ").append(balance).append("\n}").toString();
  }
}
