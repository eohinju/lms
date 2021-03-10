package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeductionReqestDto {

    private int compNumber;
    private String serviceNumber;
    private String rank;
    private String name;
    private double balance;
    private double deductions;
    private boolean joined;

}
