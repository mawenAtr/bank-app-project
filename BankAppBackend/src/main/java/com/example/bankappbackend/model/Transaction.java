package com.example.bankappbackend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_account_number")
    private String fromAccountNumber;

    @Column(name = "to_account_number")
    private String toAccountNumber;

    private BigDecimal amount;
    private String title;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    private String status;

    public Transaction() {}

    public Transaction(String fromAccountNumber, String toAccountNumber, BigDecimal amount,
                       String title, String receiverName, LocalDateTime transactionDate, String status) {
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

    @Transient
    public String getDate() {
        if (transactionDate == null) {
            return null;
        }
        return transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Transient
    public String getType() {
        return "TRANSFER";
    }

    @Transient
    public String getDescription() {
        return title != null ? title : "Bank Transfer";
    }
}