package com.example.bankappbackend.controller;

import com.example.bankappbackend.dto.*;
import com.example.bankappbackend.model.User;
import com.example.bankappbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.authenticateUser(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/deposit/pln")
    public ResponseEntity<?> depositPln(@RequestBody DepositRequest request) {
        try {
            User user = userService.depositPln(request.getEmail(), request.getAmount());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/deposit/eur")
    public ResponseEntity<?> depositEur(@RequestBody DepositRequest request) {
        try {
            User user = userService.depositEur(request.getEmail(), request.getAmount());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/deposit/usd")
    public ResponseEntity<?> depositUsd(@RequestBody DepositRequest request) {
        try {
            User user = userService.depositUsd(request.getEmail(), request.getAmount());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/withdraw/pln")
    public ResponseEntity<?> withdrawPln(@RequestBody WithdrawRequest request) {
        try {
            User user = userService.withdrawPln(request.getEmail(), request.getAmount());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/withdraw/eur")
    public ResponseEntity<?> withdrawEur(@RequestBody WithdrawRequest request) {
        try {
            User user = userService.withdrawEur(request.getEmail(), request.getAmount());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/withdraw/usd")
    public ResponseEntity<?> withdrawUsd(@RequestBody WithdrawRequest request) {
        try {
            User user = userService.withdrawUsd(request.getEmail(), request.getAmount());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/exchange")
    public ResponseEntity<?> exchangeCurrency(
            @PathVariable Long userId,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam BigDecimal amount) {

        try {
            UserResponse response = userService.exchangeCurrency(userId, fromCurrency, toCurrency, amount);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/email/{email}/response")
    public ResponseEntity<?> getUserResponseByEmail(@PathVariable String email) {
        try {
            UserResponse user = userService.getUserResponseByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/balances")
    public ResponseEntity<?> getAllBalances(@PathVariable Long userId) {
        try {
            Map<String, BigDecimal> balances = userService.getAllBalances(userId);
            return ResponseEntity.ok(balances);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestParam String email) {
        try {
            User user = userService.findByEmail(email);

            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequest request) {
        try {
            userService.updateUserProfile(request.getEmail(), request.getPhoneNumber());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            userService.changePassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}