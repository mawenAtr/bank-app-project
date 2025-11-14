package com.example.bankappbackend.controller;

import com.example.bankappbackend.model.Transaction;
import com.example.bankappbackend.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransferService transferService;

    public TransactionController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "30") int days) {

        List<Transaction> transactions = transferService.getTransactionsByAccount(accountNumber, days);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/history/{accountNumber}/filter")
    public ResponseEntity<List<Transaction>> getFilteredTransactions(
            @PathVariable String accountNumber,
            @RequestParam String type, // "all", "income", "expenses"
            @RequestParam(defaultValue = "30") int days) {

        List<Transaction> transactions = transferService.getFilteredTransactions(accountNumber, type, days);
        return ResponseEntity.ok(transactions);
    }
}