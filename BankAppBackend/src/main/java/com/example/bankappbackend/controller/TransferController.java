package com.example.bankappbackend.controller;

import com.example.bankappbackend.dto.TransferRequest;
import com.example.bankappbackend.dto.TransferResponse;
import com.example.bankappbackend.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/execute")
    public ResponseEntity<?> executeTransfer(@RequestBody TransferRequest transferRequest) {
        try {
            TransferResponse response = transferService.executeTransfer(transferRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}