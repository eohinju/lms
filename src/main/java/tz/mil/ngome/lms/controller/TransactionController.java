package tz.mil.ngome.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.mil.ngome.lms.dto.TransactionDto;
import tz.mil.ngome.lms.service.TransactionService;
import tz.mil.ngome.lms.utils.Response;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin( origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "api/")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping(value = "report/transactions")
    private ResponseEntity<?> reportLoans() {
        return this.transactionService.getTransactionsReport();
    }

    @GetMapping(value = "report/member-statement/{compNumber}")
    private ResponseEntity<?> reportMember(@PathVariable(name = "compNumber", required = true) Integer compNumber) {
        return this.transactionService.getMemberTransactionsReport(compNumber);
    }

    @GetMapping(value = "get-transactions")
    private Response<List<TransactionDto>> transactions() {
        return this.transactionService.getTransactions(LocalDate.parse("2020-09-01"),LocalDate.parse("2020-09-02"));
    }
}
