package com.example.bankappbackend.repository;

import com.example.bankappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPesel(String pesel);
    Optional<User> findByEmail(String email);
    Optional<User> findByAccountNumber(String accountNumber);

    @Query("SELECT u.balancePln FROM User u WHERE u.id = :userId")
    BigDecimal findBalancePlnByUserId(@Param("userId") Long userId);

    @Query("SELECT u.balanceEur FROM User u WHERE u.id = :userId")
    BigDecimal findBalanceEurByUserId(@Param("userId") Long userId);

    @Query("SELECT u.balanceUsd FROM User u WHERE u.id = :userId")
    BigDecimal findBalanceUsdByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN u.balancePln >= :amount THEN true ELSE false END FROM User u WHERE u.id = :userId")
    boolean hasSufficientPln(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    @Query("SELECT CASE WHEN u.balanceEur >= :amount THEN true ELSE false END FROM User u WHERE u.id = :userId")
    boolean hasSufficientEur(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    @Query("SELECT CASE WHEN u.balanceUsd >= :amount THEN true ELSE false END FROM User u WHERE u.id = :userId")
    boolean hasSufficientUsd(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}