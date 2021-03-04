package tz.mil.ngome.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.entity.LoanReturn;

@Setter
@Getter
@AllArgsConstructor
public class ReturnReportDataDto {

    private LoanReturn.ReturnStatus status;
    private String name;
    private double amount;

    public String toString(){
        return new StringBuilder().append("ReturnReportDataDto{\n")
                .append("\tstatus: "+status+"\n")
                .append("\tname: "+name+"\n")
                .append("\tamount"+amount+"\n}\n").toString();
    }

}
