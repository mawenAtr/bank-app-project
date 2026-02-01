package com.example.bankapp.models;

public class CurrencyExchangeRequest {
    private String userEmail;
    private String fromCurrency;
    private String toCurrency;
    private double amount;

    public CurrencyExchangeRequest() {}

    public CurrencyExchangeRequest(String userEmail, String fromCurrency, String toCurrency, double amount) {
        this.userEmail = userEmail;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
    }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}