package com.example.bankappbackend.repository;

import com.example.bankappbackend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE (t.fromAccountNumber = :accountNumber OR t.toAccountNumber = :accountNumber) AND t.transactionDate >= :sinceDate ORDER BY t.transactionDate DESC")
    List<Transaction> findByAccountAndDate(@Param("accountNumber") String accountNumber,
                                           @Param("sinceDate") LocalDateTime sinceDate);
}