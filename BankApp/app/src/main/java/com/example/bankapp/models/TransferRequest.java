package com.example.bankapp.models;

public class TransferRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String title;
    private String receiverName;
    private String address;

    public TransferRequest(String fromAccountNumber, String toAccountNumber, double amount,
                           String title, String receiverName, String address) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.title = title;
        this.receiverName = receiverName;
        this.address = address;
    }


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

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}