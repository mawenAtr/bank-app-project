package com.example.bankappbackend.dto;

public class WithdrawRequest {
    private String email;
    private double amount;

    public WithdrawRequest() {}

    public WithdrawRequest(String email, double amount) {
        this.email = email;
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}