package com.example.bankappbackend.dto;

public class PinChangeRequest {
    private String currentPin;
    private String newPin;
    private String confirmPin;

    public PinChangeRequest() {}

    public PinChangeRequest(String currentPin, String newPin, String confirmPin) {
        this.currentPin = currentPin;
        this.newPin = newPin;
        this.confirmPin = confirmPin;
    }

    public String getCurrentPin() { return currentPin; }
    public void setCurrentPin(String currentPin) { this.currentPin = currentPin; }

    public String getNewPin() { return newPin; }
    public void setNewPin(String newPin) { this.newPin = newPin; }

    public String getConfirmPin() { return confirmPin; }
    public void setConfirmPin(String confirmPin) { this.confirmPin = confirmPin; }
}