package com.example.bankapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bankapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.snackbar.Snackbar;
import java.util.Random;
import java.util.regex.Pattern;

public class RecoveryPasswordActivity extends AppCompatActivity {

    private TextInputEditText inputEmail;
    private Button btnResetPassword;
    private TextView tvNewPassword;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_password);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        inputEmail = findViewById(R.id.input_email_or_account);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        tvNewPassword = findViewById(R.id.tv_new_password);
    }

    private void setupClickListeners() {
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = inputEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showSnackbar("Please enter email address");
            return;
        }

        if (!isValidEmail(email)) {
            showSnackbar("Please enter a valid email address");
            return;
        }

        processPasswordReset(email);
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private void processPasswordReset(String email) {
        showSnackbar("Processing your request...");
        btnResetPassword.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String newPassword = generateNewPassword();
                displayNewPassword(newPassword);
                btnResetPassword.setEnabled(true);
                showSnackbar("Password reset successfully!");
                sendPasswordResetEmail(email, newPassword);
            }
        }, 1000);
    }

    private String generateNewPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder newPassword = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            newPassword.append(characters.charAt(random.nextInt(characters.length())));
        }

        return newPassword.toString();
    }

    private void displayNewPassword(String newPassword) {
        tvNewPassword.setText("Your new password: " + newPassword);
        tvNewPassword.setVisibility(View.VISIBLE);
    }

    private void sendPasswordResetEmail(String email, String newPassword) {
        System.out.println("Sending password reset to: " + email);
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}