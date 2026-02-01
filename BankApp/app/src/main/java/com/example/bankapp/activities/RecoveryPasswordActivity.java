package com.example.bankapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bankapp.R;
import com.example.bankapp.api.ApiService;
import com.example.bankapp.api.RetrofitClient;
import com.example.bankapp.models.PasswordChangeRequest;
import com.google.android.material.textfield.TextInputEditText;
import android.text.TextUtils;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoveryPasswordActivity extends AppCompatActivity {

    private TextInputEditText inputEmail, inputNewPassword, inputConfirmPassword;
    private Button btnResetPassword;
    private ApiService apiService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_password);
        apiService = RetrofitClient.getApiService();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        inputEmail = findViewById(R.id.input_email);
        inputNewPassword = findViewById(R.id.input_new_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);
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
        String newPassword = inputNewPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(newPassword)) {
            Toast.makeText(this,
                    "Password must have:\n• 8+ characters\n• 1 uppercase letter\n• 1 number\n• 1 special character",
                    Toast.LENGTH_LONG).show();
            return;
        }


        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        changePassword(email, newPassword);
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;

        boolean hasUpper = false, hasLower = false,
                hasDigit = false, hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }


    private void changePassword(String email, String newPassword) {

        PasswordChangeRequest passwordRequest = new PasswordChangeRequest(email, newPassword);


        Call<Void> call = apiService.changePassword(passwordRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("RESET PASSWORD");

                if (response.isSuccessful()) {
                    Toast.makeText(RecoveryPasswordActivity.this,
                            "Password reset successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("RESET PASSWORD");
                Toast.makeText(RecoveryPasswordActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}