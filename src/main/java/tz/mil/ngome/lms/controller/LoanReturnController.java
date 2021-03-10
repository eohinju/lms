package tz.mil.ngome.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.mil.ngome.lms.service.ReturnService;

@CrossOrigin( origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "api/")
public class LoanReturnController {

    @Autowired
    ReturnService returnService;

    @GetMapping(value = "report/returns")
    private ResponseEntity<?> reportReturns(@RequestParam(name = "month", required = true) String month) {
        return this.returnService.getReturnsReport(month);
    }

    @GetMapping(value = "report/deductions")
    private ResponseEntity<?> reportDeductions() {
        return this.returnService.getUnpaidLoans();
    }

}
