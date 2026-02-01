package com.example.bankappbackend.service;

import com.example.bankappbackend.dto.TransferRequest;
import com.example.bankappbackend.dto.TransferResponse;
import com.example.bankappbackend.model.Transaction;
import com.example.bankappbackend.model.User;
import com.example.bankappbackend.repository.TransactionRepository;
import com.example.bankappbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransferService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransferService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransferResponse executeTransfer(TransferRequest transferRequest) {
        Optional<User> senderOptional = userRepository.findByAccountNumber(transferRequest.getFromAccountNumber());
        if (senderOptional.isEmpty()) {
            return new TransferResponse(false, "Sender account not found",
                    transferRequest.getAmount(), 0.0);
        }
        User sender = senderOptional.get();

        Optional<User> receiverOptional = userRepository.findByAccountNumber(transferRequest.getToAccountNumber());
        if (receiverOptional.isEmpty()) {
            BigDecimal senderBalancePln = sender.getBalancePln() != null ? sender.getBalancePln() : BigDecimal.ZERO;
            return new TransferResponse(false, "Receiver account not found",
                    transferRequest.getAmount(), senderBalancePln.doubleValue());
        }
        User receiver = receiverOptional.get();

        if (transferRequest.getFromAccountNumber().equals(transferRequest.getToAccountNumber())) {
            BigDecimal senderBalancePln = sender.getBalancePln() != null ? sender.getBalancePln() : BigDecimal.ZERO;
            return new TransferResponse(false,
                    "You cannot transfer money to your own account",
                    transferRequest.getAmount(),
                    senderBalancePln.doubleValue());
        }

        double amount = transferRequest.getAmount();
        String currency = transferRequest.getCurrency() != null ? transferRequest.getCurrency() : "PLN";

        BigDecimal senderBalance;
        BigDecimal receiverBalance;

        // Pobierz saldo w odpowiedniej walucie
        switch (currency.toUpperCase()) {
            case "EUR":
                senderBalance = sender.getBalanceEur() != null ? sender.getBalanceEur() : BigDecimal.ZERO;
                receiverBalance = receiver.getBalanceEur() != null ? receiver.getBalanceEur() : BigDecimal.ZERO;
                break;
            case "USD":
                senderBalance = sender.getBalanceUsd() != null ? sender.getBalanceUsd() : BigDecimal.ZERO;
                receiverBalance = receiver.getBalanceUsd() != null ? receiver.getBalanceUsd() : BigDecimal.ZERO;
                break;
            case "PLN":
            default:
                senderBalance = sender.getBalancePln() != null ? sender.getBalancePln() : BigDecimal.ZERO;
                receiverBalance = receiver.getBalancePln() != null ? receiver.getBalancePln() : BigDecimal.ZERO;
                break;
        }

        BigDecimal transferAmount = BigDecimal.valueOf(amount);

        if (senderBalance.compareTo(transferAmount) < 0) {
            return new TransferResponse(false,
                    "Insufficient funds",
                    amount,
                    senderBalance.doubleValue());
        }

        // Odejmij od nadawcy
        BigDecimal newSenderBalance = senderBalance.subtract(transferAmount);

        // Dodaj do odbiorcy
        BigDecimal newReceiverBalance = receiverBalance.add(transferAmount);

        // Zaktualizuj salda w odpowiedniej walucie
        switch (currency.toUpperCase()) {
            case "EUR":
                sender.setBalanceEur(newSenderBalance);
                receiver.setBalanceEur(newReceiverBalance);
                break;
            case "USD":
                sender.setBalanceUsd(newSenderBalance);
                receiver.setBalanceUsd(newReceiverBalance);
                break;
            case "PLN":
            default:
                sender.setBalancePln(newSenderBalance);
                receiver.setBalancePln(newReceiverBalance);
                break;
        }

        userRepository.save(sender);
        userRepository.save(receiver);

        saveTransaction(transferRequest);

        BigDecimal finalSenderBalance;
        switch (currency.toUpperCase()) {
            case "EUR":
                finalSenderBalance = sender.getBalanceEur();
                break;
            case "USD":
                finalSenderBalance = sender.getBalanceUsd();
                break;
            case "PLN":
            default:
                finalSenderBalance = sender.getBalancePln();
                break;
        }

        return new TransferResponse(true,
                "Transfer completed",
                amount,
                finalSenderBalance.doubleValue());
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber, int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        return transactionRepository.findByAccountAndDate(accountNumber, sinceDate);
    }

    public List<Transaction> getFilteredTransactions(String accountNumber, String type, int days) {
        List<Transaction> allTransactions = getTransactionsByAccount(accountNumber, days);

        return allTransactions.stream()
                .filter(transaction -> {
                    if ("income".equals(type)) {
                        return accountNumber.equals(transaction.getToAccountNumber());
                    } else if ("expenses".equals(type)) {
                        return accountNumber.equals(transaction.getFromAccountNumber());
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public void saveTransaction(TransferRequest transferRequest) {
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber(transferRequest.getFromAccountNumber());
        transaction.setToAccountNumber(transferRequest.getToAccountNumber());
        transaction.setAmount(BigDecimal.valueOf(transferRequest.getAmount()));
        transaction.setTitle(transferRequest.getTitle());
        transaction.setReceiverName(transferRequest.getReceiverName());
        transaction.setStatus("COMPLETED");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setCurrency(transferRequest.getCurrency() != null ? transferRequest.getCurrency() : "PLN");

        transactionRepository.save(transaction);
    }
}