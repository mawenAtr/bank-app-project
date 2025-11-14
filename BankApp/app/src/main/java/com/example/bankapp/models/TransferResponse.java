package com.example.bankapp.models;

public class TransferResponse {
    private boolean success;
    private String message;
    private double amount;
    private Double newBalance;
    private String transactionId;
    private String timestamp;

    public TransferResponse(boolean success, String message, double amount,
                            Double newBalance, String transactionId, String timestamp) {
        this.success = success;
        this.message = message;
        this.amount = amount;
        this.newBalance = newBalance;
        this.transactionId = transactionId;
        this.timestamp = timestamp;
    }


    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Double getNewBalance() { return newBalance; }
    public void setNewBalance(Double newBalance) { this.newBalance = newBalance; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}