package com.example.bankapp.models;

import java.math.BigDecimal;

public class ExchangeResponse {
    private boolean success;
    private String message;
    private BigDecimal newBalancePln;
    private BigDecimal newBalanceEur;
    private BigDecimal newBalanceUsd;
    private BigDecimal rate;
    private BigDecimal commission;
    private BigDecimal exchangedAmount;

    public ExchangeResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getNewBalancePln() {
        return newBalancePln;
    }

    public void setNewBalancePln(BigDecimal newBalancePln) {
        this.newBalancePln = newBalancePln;
    }

    public BigDecimal getNewBalanceEur() {
        return newBalanceEur;
    }

    public void setNewBalanceEur(BigDecimal newBalanceEur) {
        this.newBalanceEur = newBalanceEur;
    }

    public BigDecimal getNewBalanceUsd() {
        return newBalanceUsd;
    }

    public void setNewBalanceUsd(BigDecimal newBalanceUsd) {
        this.newBalanceUsd = newBalanceUsd;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getExchangedAmount() {
        return exchangedAmount;
    }

    public void setExchangedAmount(BigDecimal exchangedAmount) {
        this.exchangedAmount = exchangedAmount;
    }
}