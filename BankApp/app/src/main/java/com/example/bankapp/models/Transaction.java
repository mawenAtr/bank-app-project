package com.example.bankapp.models;

import java.math.BigDecimal;

public class Transaction {

    private String id;
    private BigDecimal amount;
    private String description;
    private String date;
    private String type;
    private String accountNumber;
    private String formattedDate;

    public Transaction(String id, double amount, String description,
                       String date, String type, String accountNumber) {
        this.id = id;
        this.amount = BigDecimal.valueOf(amount);
        this.description = description;
        this.date = date;
        this.type = type;
        this.accountNumber = accountNumber;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}