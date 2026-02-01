package com.example.bankappbackend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String pesel;
    private String password;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    private String city;
    private String zipCode;
    private String accountNumber;

    @Column(name = "balance_pln")
    private BigDecimal balancePln = BigDecimal.ZERO;

    @Column(name = "balance_eur")
    private BigDecimal balanceEur = BigDecimal.ZERO;

    @Column(name = "balance_usd")
    private BigDecimal balanceUsd = BigDecimal.ZERO;

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public BigDecimal getBalancePln() {
        return balancePln != null ? balancePln : BigDecimal.ZERO;
    }

    public void setBalancePln(BigDecimal balancePln) {
        this.balancePln = balancePln != null ? balancePln : BigDecimal.ZERO;
    }

    public BigDecimal getBalanceEur() {
        return balanceEur != null ? balanceEur : BigDecimal.ZERO;
    }

    public void setBalanceEur(BigDecimal balanceEur) {
        this.balanceEur = balanceEur != null ? balanceEur : BigDecimal.ZERO;
    }

    public BigDecimal getBalanceUsd() {
        return balanceUsd != null ? balanceUsd : BigDecimal.ZERO;
    }

    public void setBalanceUsd(BigDecimal balanceUsd) {
        this.balanceUsd = balanceUsd != null ? balanceUsd : BigDecimal.ZERO;
    }
}