package com.example.bankappbackend.dto;

public class CardSettingsRequest {
    private long dailyLimit;
    private long transactionLimit;
    private Boolean onlinePaymentsEnabled;
    private Boolean atmWithdrawalsEnabled;

    public CardSettingsRequest() {}

    public CardSettingsRequest(long dailyLimit, long transactionLimit,
                               Boolean onlinePaymentsEnabled, Boolean atmWithdrawalsEnabled) {
        this.dailyLimit = dailyLimit;
        this.transactionLimit = transactionLimit;
        this.onlinePaymentsEnabled = onlinePaymentsEnabled;
        this.atmWithdrawalsEnabled = atmWithdrawalsEnabled;
    }

    public long getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(long dailyLimit) { this.dailyLimit = dailyLimit; }

    public long getTransactionLimit() { return transactionLimit; }
    public void setTransactionLimit(long transactionLimit) { this.transactionLimit = transactionLimit; }

    public Boolean getOnlinePaymentsEnabled() { return onlinePaymentsEnabled; }
    public void setOnlinePaymentsEnabled(Boolean onlinePaymentsEnabled) { this.onlinePaymentsEnabled = onlinePaymentsEnabled; }

    public Boolean getAtmWithdrawalsEnabled() { return atmWithdrawalsEnabled; }
    public void setAtmWithdrawalsEnabled(Boolean atmWithdrawalsEnabled) { this.atmWithdrawalsEnabled = atmWithdrawalsEnabled; }
}