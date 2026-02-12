package com.example.bankappbackend.service;

import com.example.bankappbackend.dto.UserResponse;
import com.example.bankappbackend.model.User;
import com.example.bankappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByPesel(user.getPesel())) {
            throw new RuntimeException("PESEL already exists");
        }

        user.setBalancePln(BigDecimal.ZERO);
        user.setBalanceEur(BigDecimal.ZERO);
        user.setBalanceUsd(BigDecimal.ZERO);

        return userRepository.save(user);
    }

    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    @Transactional
    public User depositPln(String email, double amount) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal depositAmount = new BigDecimal(amount);
        user.setBalancePln(user.getBalancePln().add(depositAmount));

        return userRepository.save(user);
    }

    @Transactional
    public User depositEur(String email, double amount) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal depositAmount = new BigDecimal(amount);
        user.setBalanceEur(user.getBalanceEur().add(depositAmount));

        return userRepository.save(user);
    }

    @Transactional
    public User depositUsd(String email, double amount) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal depositAmount = new BigDecimal(amount);
        user.setBalanceUsd(user.getBalanceUsd().add(depositAmount));

        return userRepository.save(user);
    }

    @Transactional
    public User withdrawPln(String email, double amount) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal withdrawAmount = new BigDecimal(amount);

        if (user.getBalancePln().compareTo(withdrawAmount) < 0) {
            throw new RuntimeException("Insufficient funds in PLN");
        }

        user.setBalancePln(user.getBalancePln().subtract(withdrawAmount));
        return userRepository.save(user);
    }

    @Transactional
    public User withdrawEur(String email, double amount) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal withdrawAmount = new BigDecimal(amount);

        if (user.getBalanceEur().compareTo(withdrawAmount) < 0) {
            throw new RuntimeException("Insufficient funds in EUR");
        }

        user.setBalanceEur(user.getBalanceEur().subtract(withdrawAmount));
        return userRepository.save(user);
    }

    @Transactional
    public User withdrawUsd(String email, double amount) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal withdrawAmount = new BigDecimal(amount);

        if (user.getBalanceUsd().compareTo(withdrawAmount) < 0) {
            throw new RuntimeException("Insufficient funds in USD");
        }

        user.setBalanceUsd(user.getBalanceUsd().subtract(withdrawAmount));
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserResponse getUserResponseByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToResponse(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public void updateUserProfile(String email, String phoneNumber) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPhoneNumber(phoneNumber);
            userRepository.save(user);
        }
    }

    @Transactional
    public void changePassword(String email, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(newPassword);
            userRepository.save(user);
        }
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToResponse(user);
    }

    public Map<String, BigDecimal> getAllBalances(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, BigDecimal> balances = new HashMap<>();
        balances.put("PLN", user.getBalancePln());
        balances.put("EUR", user.getBalanceEur());
        balances.put("USD", user.getBalanceUsd());

        return balances;
    }

    @Transactional
    public UserResponse exchangeCurrency(Long userId, String fromCurrency, String toCurrency, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal fromBalance;
        switch (fromCurrency.toUpperCase()) {
            case "PLN":
                fromBalance = user.getBalancePln();
                break;
            case "EUR":
                fromBalance = user.getBalanceEur();
                break;
            case "USD":
                fromBalance = user.getBalanceUsd();
                break;
            default:
                throw new RuntimeException("Unsupported source currency: " + fromCurrency);
        }

        if (fromBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds in " + fromCurrency);
        }

        BigDecimal exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        BigDecimal receivedAmount = amount.multiply(exchangeRate);

        switch (fromCurrency.toUpperCase()) {
            case "PLN":
                user.setBalancePln(user.getBalancePln().subtract(amount));
                break;
            case "EUR":
                user.setBalanceEur(user.getBalanceEur().subtract(amount));
                break;
            case "USD":
                user.setBalanceUsd(user.getBalanceUsd().subtract(amount));
                break;
        }

        switch (toCurrency.toUpperCase()) {
            case "PLN":
                user.setBalancePln(user.getBalancePln().add(receivedAmount));
                break;
            case "EUR":
                user.setBalanceEur(user.getBalanceEur().add(receivedAmount));
                break;
            case "USD":
                user.setBalanceUsd(user.getBalanceUsd().add(receivedAmount));
                break;
        }

        userRepository.save(user);
        return convertToResponse(user);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setPesel(user.getPesel());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setCity(user.getCity());
        response.setZipCode(user.getZipCode());
        response.setAccountNumber(user.getAccountNumber());

        response.setBalancePln(user.getBalancePln());
        response.setBalanceEur(user.getBalanceEur());
        response.setBalanceUsd(user.getBalanceUsd());

        response.setDisplayCurrency("PLN");

        return response;
    }

    private BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("PLN_EUR", new BigDecimal("0.22"));
        rates.put("PLN_USD", new BigDecimal("0.25"));
        rates.put("EUR_PLN", new BigDecimal("4.55"));
        rates.put("EUR_USD", new BigDecimal("1.09"));
        rates.put("USD_PLN", new BigDecimal("4.00"));
        rates.put("USD_EUR", new BigDecimal("0.92"));

        String key = fromCurrency.toUpperCase() + "_" + toCurrency.toUpperCase();
        BigDecimal rate = rates.get(key);

        if (rate == null) {
            throw new RuntimeException("Exchange rate not available for " + key);
        }

        return rate;
    }
}