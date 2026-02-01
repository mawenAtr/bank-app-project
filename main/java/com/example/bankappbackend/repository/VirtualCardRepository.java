package com.example.bankappbackend.repository;

import com.example.bankappbackend.model.VirtualCard;
import com.example.bankappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VirtualCardRepository extends JpaRepository<VirtualCard, Long> {
    Optional<VirtualCard> findByCardNumber(String cardNumber);
    Optional<VirtualCard> findByUserId(Long userId);
    List<VirtualCard> findAllByUser(User user);
    boolean existsByUserId(Long userId);
    boolean existsByCardNumber(String cardNumber);

    @Query("SELECT v FROM VirtualCard v WHERE v.user.id = :userId AND v.isActive = true")
    List<VirtualCard> findActiveCardsByUserId(@Param("userId") Long userId);

    @Query("SELECT v FROM VirtualCard v WHERE v.user.id = :userId AND v.isBlocked = true")
    List<VirtualCard> findBlockedCardsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(v) FROM VirtualCard v WHERE v.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}