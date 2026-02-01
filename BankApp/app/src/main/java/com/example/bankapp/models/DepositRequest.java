package com.example.bankapp.models;

public class DepositRequest {
    private String email;
    private double amount;
    private String currency; // Dodaj to pole

    public DepositRequest() {}

    public DepositRequest(String email, double amount) {
        this.email = email;
        this.amount = amount;
        this.currency = "PLN";
    }

    public DepositRequest(String email, double amount, String currency) {
        this.email = email;
        this.amount = amount;
        this.currency = currency;
    }


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}