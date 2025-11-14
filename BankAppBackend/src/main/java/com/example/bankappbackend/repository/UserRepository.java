package com.example.bankappbackend.repository;

import com.example.bankappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPesel(String pesel);
    Optional<User> findByEmail(String email);

    Optional<User> findByAccountNumber(String accountNumber);
}