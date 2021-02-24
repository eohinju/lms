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
	private int amount;
	private boolean checked = false;
	
	public DeductionsRequiredDto(String loan, int amount) {
		this.loan = loan;
		this.amount = amount;
	}

	public String toString() {

		return new StringBuilder().append("DeductionsRequiredDto{\n").append("\tloan: "+loan+",\n").append("\tamount: ")
				.append(amount).append("\n}").toString();
  }
}
