package com.example.bankappbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "virtual_cards")
public class VirtualCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false, unique = true)
    private String cardNumber;

    @Column(name = "cvv", nullable = false)
    private String cvv;

    @Column(name = "expiry_date", nullable = false)
    private String expiryDate;

    @Column(name = "pin", nullable = false)
    private String pin;

    @Column(name = "daily_limit")
    private long dailyLimit = 20000;

    @Column(name = "transaction_limit")
    private long transactionLimit = 5000;

    @Column(name = "online_payments_enabled")
    private Boolean onlinePaymentsEnabled = true;

    @Column(name = "atm_withdrawals_enabled")
    private Boolean atmWithdrawalsEnabled = true;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}