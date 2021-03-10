package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatementDto {
    
    private int compNumber;
    private String serviceNumber;
    private String rank;
    private String firstname;
    private String middlename;
    private String lastname;
    private LocalDate date;
    private String narration;
    private double debit;
    private double credit;
    
}
