package com.example.bankapp.models;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

public class Account {
    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;
    private String accountType;
    private String ownerId;
    private Date openingDate;
    private boolean isActive;

    public Account() {
        this.balance = BigDecimal.ZERO;
        this.currency = Currency.getInstance("PLN");
        this.isActive = true;
    }

    public Account(String accountNumber, BigDecimal initialBalance, String ownerId) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.currency = Currency.getInstance("PLN");
        this.ownerId = ownerId;
        this.isActive = true;
        this.openingDate = new Date();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Date openingDate) {
        this.openingDate = openingDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(amount);
        }
    }

    public boolean withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 &&
                this.balance.compareTo(amount) >= 0) {
            this.balance = this.balance.subtract(amount);
            return true;
        }
        return false;
    }

    public String getFormattedBalance() {
        return String.format("%.2f %s", balance, currency.getCurrencyCode());
    }
}