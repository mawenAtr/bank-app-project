package com.example.bankapp.models;

import java.math.BigDecimal;

public class ExchangeRequest {
    private String userEmail;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal amount;
    private BigDecimal amountAfterCommission;
    private BigDecimal commission;
    private BigDecimal result;
    private BigDecimal rate;
    private String accountNumber;

    public ExchangeRequest() {}

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountAfterCommission() {
        return amountAfterCommission;
    }

    public void setAmountAfterCommission(BigDecimal amountAfterCommission) {
        this.amountAfterCommission = amountAfterCommission;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}