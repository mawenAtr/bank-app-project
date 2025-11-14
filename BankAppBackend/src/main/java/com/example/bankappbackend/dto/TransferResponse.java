package com.example.bankappbackend.dto;

public class TransferResponse {
    private boolean success;
    private String message;
    private double amount;
    private Double newBalance;

    public TransferResponse(boolean success, String message, double amount, Double newBalance) {
        this.success = success;
        this.message = message;
        this.amount = amount;
        this.newBalance = newBalance;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Double getNewBalance() { return newBalance; }
    public void setNewBalance(Double newBalance) { this.newBalance = newBalance; }
}