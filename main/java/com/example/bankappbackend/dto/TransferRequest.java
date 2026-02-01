package com.example.bankappbackend.dto;

public class TransferRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String title;
    private String receiverName;
    private String currency;

    public String getFromAccountNumber() { return fromAccountNumber; }
    public void setFromAccountNumber(String fromAccountNumber) { this.fromAccountNumber = fromAccountNumber; }

    public String getToAccountNumber() { return toAccountNumber; }
    public void setToAccountNumber(String toAccountNumber) { this.toAccountNumber = toAccountNumber; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}