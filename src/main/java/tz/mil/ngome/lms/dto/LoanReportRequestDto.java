package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.entity.Loan;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoanReportRequestDto {

    private Loan.LoanStatus status;
    private String month;

}
