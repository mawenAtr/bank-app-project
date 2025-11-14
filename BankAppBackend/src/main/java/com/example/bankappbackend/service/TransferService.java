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

        Optional<User> senderOptional =
                userRepository.findByAccountNumber(transferRequest.getFromAccountNumber());

        if (senderOptional.isEmpty()) {
            return new TransferResponse(false, "Sender account not found",
                    transferRequest.getAmount(), 0.0);
        }
        User sender = senderOptional.get();

        Optional<User> receiverOptional =
                userRepository.findByAccountNumber(transferRequest.getToAccountNumber());

        if (receiverOptional.isEmpty()) {
            return new TransferResponse(false, "Receiver account not found",
                    transferRequest.getAmount(), sender.getBalance());
        }
        User receiver = receiverOptional.get();

        if (transferRequest.getFromAccountNumber().equals(transferRequest.getToAccountNumber())) {
            return new TransferResponse(false,
                    "You cannot transfer money to your own account",
                    transferRequest.getAmount(),
                    sender.getBalance());
        }

        double amount = transferRequest.getAmount();
        double senderBalance = sender.getBalance() != null ? sender.getBalance() : 0.0;

        if (senderBalance < amount) {
            return new TransferResponse(false,
                    "Insufficient funds",
                    amount,
                    senderBalance);
        }

        sender.setBalance(senderBalance - amount);
        receiver.setBalance((receiver.getBalance() != null ? receiver.getBalance() : 0.0) + amount);

        userRepository.save(sender);
        userRepository.save(receiver);

        saveTransaction(transferRequest);

        return new TransferResponse(true,
                "Transfer completed",
                amount,
                sender.getBalance());
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

        transactionRepository.save(transaction);
    }
}
