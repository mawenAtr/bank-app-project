package com.example.bankappbackend.dto;

import java.math.BigDecimal;
import java.util.Date;

public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String pesel;
    private Date dateOfBirth;
    private String city;
    private String zipCode;
    private String accountNumber;

    private BigDecimal balancePln;
    private BigDecimal balanceEur;
    private BigDecimal balanceUsd;


    private String displayCurrency = "PLN";


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPesel() { return pesel; }
    public void setPesel(String pesel) { this.pesel = pesel; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public BigDecimal getBalancePln() { return balancePln; }
    public void setBalancePln(BigDecimal balancePln) { this.balancePln = balancePln; }

    public BigDecimal getBalanceEur() { return balanceEur; }
    public void setBalanceEur(BigDecimal balanceEur) { this.balanceEur = balanceEur; }

    public BigDecimal getBalanceUsd() { return balanceUsd; }
    public void setBalanceUsd(BigDecimal balanceUsd) { this.balanceUsd = balanceUsd; }

    public String getDisplayCurrency() { return displayCurrency; }
    public void setDisplayCurrency(String displayCurrency) { this.displayCurrency = displayCurrency; }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}