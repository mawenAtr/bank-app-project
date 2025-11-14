package com.example.bankappbackend.dto;

public class PasswordChangeRequest {
    private String email;
    private String newPassword;

    public PasswordChangeRequest() {}

    public PasswordChangeRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}