package com.example.bankappbackend.service;

import com.example.bankappbackend.dto.*;
import com.example.bankappbackend.model.VirtualCard;
import com.example.bankappbackend.model.User;
import com.example.bankappbackend.repository.UserRepository;
import com.example.bankappbackend.repository.VirtualCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class VirtualCardService {

    @Autowired
    private VirtualCardRepository virtualCardRepository;

    @Autowired
    private UserRepository userRepository;

    private final SecureRandom random = new SecureRandom();

    public VirtualCardResponse createVirtualCard(VirtualCardCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (virtualCardRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("User already has a virtual card");
        }

        VirtualCard virtualCard = new VirtualCard();
        virtualCard.setUser(user);
        virtualCard.setCardNumber(generateCardNumber());
        virtualCard.setCvv(generateCVV());
        virtualCard.setExpiryDate(generateExpiryDate());
        virtualCard.setPin(request.getPin());
        virtualCard.setIsActive(true);
        virtualCard.setIsBlocked(false);

        virtualCardRepository.save(virtualCard);
        return convertToResponse(virtualCard);
    }

    public VirtualCardResponse getVirtualCardByUserId(Long userId) {
        VirtualCard virtualCard = virtualCardRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Virtual card not found"));
        return convertToResponse(virtualCard);
    }

    public VirtualCardResponse getVirtualCardById(Long cardId) {
        VirtualCard virtualCard = virtualCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Virtual card not found"));
        return convertToResponse(virtualCard);
    }

    public VirtualCardResponse updateCardSettings(Long cardId, CardSettingsRequest settingsRequest) {
        VirtualCard virtualCard = virtualCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Virtual card not found"));

        virtualCard.setDailyLimit(settingsRequest.getDailyLimit());
        virtualCard.setTransactionLimit(settingsRequest.getTransactionLimit());
        virtualCard.setOnlinePaymentsEnabled(settingsRequest.getOnlinePaymentsEnabled());
        virtualCard.setAtmWithdrawalsEnabled(settingsRequest.getAtmWithdrawalsEnabled());

        virtualCardRepository.save(virtualCard);
        return convertToResponse(virtualCard);
    }

    public VirtualCardResponse updatePin(Long cardId, PinChangeRequest pinChangeRequest) {
        VirtualCard virtualCard = virtualCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Virtual card not found"));

        if (!virtualCard.getPin().equals(pinChangeRequest.getCurrentPin())) {
            throw new RuntimeException("Current PIN is incorrect");
        }

        if (!pinChangeRequest.getNewPin().equals(pinChangeRequest.getConfirmPin())) {
            throw new RuntimeException("New PINs do not match");
        }

        virtualCard.setPin(pinChangeRequest.getNewPin());
        virtualCardRepository.save(virtualCard);
        return convertToResponse(virtualCard);
    }

    public VirtualCardResponse blockCard(Long cardId) {
        VirtualCard virtualCard = virtualCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Virtual card not found"));

        virtualCard.setIsBlocked(true);
        virtualCard.setIsActive(false);
        virtualCardRepository.save(virtualCard);
        return convertToResponse(virtualCard);
    }

    public VirtualCardResponse unblockCard(Long cardId) {
        VirtualCard virtualCard = virtualCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Virtual card not found"));

        virtualCard.setIsBlocked(false);
        virtualCard.setIsActive(true);
        virtualCardRepository.save(virtualCard);
        return convertToResponse(virtualCard);
    }

    public boolean userHasVirtualCard(Long userId) {
        return virtualCardRepository.findByUserId(userId).isPresent();
    }

    private String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }

        String generated = cardNumber.toString();
        while (virtualCardRepository.existsByCardNumber(generated)) {
            cardNumber = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                cardNumber.append(random.nextInt(10));
            }
            generated = cardNumber.toString();
        }

        return generated;
    }

    private String generateCVV() {
        StringBuilder cvv = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            cvv.append(random.nextInt(10));
        }
        return cvv.toString();
    }

    private String generateExpiryDate() {
        LocalDate now = LocalDate.now();
        LocalDate expiry = now.plusYears(3);
        return expiry.format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    private VirtualCardResponse convertToResponse(VirtualCard virtualCard) {
        VirtualCardResponse response = new VirtualCardResponse();
        response.setId(virtualCard.getId());
        response.setCardNumber(virtualCard.getCardNumber());
        response.setCvv(virtualCard.getCvv());
        response.setExpiryDate(virtualCard.getExpiryDate());
        response.setDailyLimit(virtualCard.getDailyLimit());
        response.setTransactionLimit(virtualCard.getTransactionLimit());
        response.setOnlinePaymentsEnabled(virtualCard.getOnlinePaymentsEnabled());
        response.setAtmWithdrawalsEnabled(virtualCard.getAtmWithdrawalsEnabled());
        response.setIsActive(virtualCard.getIsActive());
        response.setIsBlocked(virtualCard.getIsBlocked());
        return response;
    }
}