package com.example.bankapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankapp.R;
import com.example.bankapp.api.ApiService;
import com.example.bankapp.api.RetrofitClient;
import com.example.bankapp.models.LoginRequest;
import com.example.bankapp.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerLink;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        apiService = RetrofitClient.getApiService();

        loginButton.setOnClickListener(v -> loginUser());
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });

        checkIfUserIsLoggedIn();

    }

    private void initializeViews() {
        emailInput = findViewById(R.id.account_number_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);

        emailInput.setHint("Email");
        passwordInput.setHint("Password");
    }

    private void checkIfUserIsLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        String userEmail = prefs.getString("user_email", null);

        if (isLoggedIn && userEmail != null) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
            finish();
        }
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Valid email address", Toast.LENGTH_SHORT).show();
            emailInput.requestFocus();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);

        Call<User> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    saveUserData(user);
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.putExtra("email", user.getEmail());
                    intent.putExtra("ACCOUNT_NUMBER", user.getAccountNumber());
                    startActivity(intent);
                    finish();
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login error: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserData(User user) {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("user_id", user.getId());
        editor.putString("user_email", user.getEmail());
        editor.putString("first_name", user.getFirstName());
        editor.putString("last_name", user.getLastName());
        editor.putString("account_number", user.getAccountNumber());
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    public void onForgotPasswordClick(View view) {
        Intent intent = new Intent(LoginActivity.this, RecoveryPasswordActivity.class);
        startActivity(intent);
    }
}