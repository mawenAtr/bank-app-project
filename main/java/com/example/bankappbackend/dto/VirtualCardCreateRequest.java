package com.example.bankappbackend.dto;

public class VirtualCardCreateRequest {
    private Long userId;
    private String pin;

    public VirtualCardCreateRequest() {}

    public VirtualCardCreateRequest(Long userId, String pin) {
        this.userId = userId;
        this.pin = pin;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}