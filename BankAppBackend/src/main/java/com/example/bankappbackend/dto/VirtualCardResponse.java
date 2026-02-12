package com.example.bankappbackend.dto;

public class VirtualCardResponse {
    private Long id;
    private String cardNumber;
    private String cvv;
    private String expiryDate;
    private long dailyLimit;
    private long transactionLimit;
    private Boolean onlinePaymentsEnabled;
    private Boolean atmWithdrawalsEnabled;
    private Boolean isActive;
    private Boolean isBlocked;

    public VirtualCardResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public long getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(long dailyLimit) { this.dailyLimit = dailyLimit; }

    public long getTransactionLimit() { return transactionLimit; }
    public void setTransactionLimit(long transactionLimit) { this.transactionLimit = transactionLimit; }

    public Boolean getOnlinePaymentsEnabled() { return onlinePaymentsEnabled; }
    public void setOnlinePaymentsEnabled(Boolean onlinePaymentsEnabled) { this.onlinePaymentsEnabled = onlinePaymentsEnabled; }

    public Boolean getAtmWithdrawalsEnabled() { return atmWithdrawalsEnabled; }
    public void setAtmWithdrawalsEnabled(Boolean atmWithdrawalsEnabled) { this.atmWithdrawalsEnabled = atmWithdrawalsEnabled; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsBlocked() { return isBlocked; }
    public void setIsBlocked(Boolean isBlocked) { this.isBlocked = isBlocked; }
}