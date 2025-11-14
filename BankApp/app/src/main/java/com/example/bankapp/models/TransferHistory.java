package com.example.bankapp.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferHistory {
    private Long id;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String title;
    private String receiverName;
    private LocalDateTime transactionDate;
    private String status;


    public TransferHistory() {}

    public TransferHistory(String fromAccountNumber, String toAccountNumber,
                           BigDecimal amount, String title, String receiverName,
                           LocalDateTime transactionDate, String status) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.title = title;
        this.receiverName = receiverName;
        this.transactionDate = transactionDate;
        this.status = status;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFromAccountNumber() { return fromAccountNumber; }
    public void setFromAccountNumber(String fromAccountNumber) { this.fromAccountNumber = fromAccountNumber; }

    public String getToAccountNumber() { return toAccountNumber; }
    public void setToAccountNumber(String toAccountNumber) { this.toAccountNumber = toAccountNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}