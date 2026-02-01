package com.example.bankappbackend.controller;

import com.example.bankappbackend.dto.*;
import com.example.bankappbackend.service.VirtualCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/virtual-cards")
public class VirtualCardController {

    @Autowired
    private VirtualCardService virtualCardService;

    @PostMapping("/create")
    public ResponseEntity<?> createVirtualCard(@RequestBody VirtualCardCreateRequest request) {
        try {
            VirtualCardResponse card = virtualCardService.createVirtualCard(request);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getVirtualCardByUserId(@PathVariable Long userId) {
        try {
            VirtualCardResponse card = virtualCardService.getVirtualCardByUserId(userId);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<?> getVirtualCardById(@PathVariable Long cardId) {
        try {
            VirtualCardResponse card = virtualCardService.getVirtualCardById(cardId);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<?> checkIfUserHasCard(@PathVariable Long userId) {
        try {
            boolean hasCard = virtualCardService.userHasVirtualCard(userId);
            return ResponseEntity.ok(hasCard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{cardId}/settings")
    public ResponseEntity<?> updateCardSettings(@PathVariable Long cardId, @RequestBody CardSettingsRequest request) {
        try {
            VirtualCardResponse card = virtualCardService.updateCardSettings(cardId, request);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{cardId}/pin")
    public ResponseEntity<?> updatePin(@PathVariable Long cardId, @RequestBody PinChangeRequest request) {
        try {
            VirtualCardResponse card = virtualCardService.updatePin(cardId, request);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{cardId}/block")
    public ResponseEntity<?> blockCard(@PathVariable Long cardId) {
        try {
            VirtualCardResponse card = virtualCardService.blockCard(cardId);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{cardId}/unblock")
    public ResponseEntity<?> unblockCard(@PathVariable Long cardId) {
        try {
            VirtualCardResponse card = virtualCardService.unblockCard(cardId);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}