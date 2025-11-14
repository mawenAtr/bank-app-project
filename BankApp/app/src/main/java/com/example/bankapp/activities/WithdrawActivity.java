package com.example.bankapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bankapp.R;
import com.example.bankapp.api.ApiService;
import com.example.bankapp.api.RetrofitClient;
import com.example.bankapp.models.User;
import com.example.bankapp.models.WithdrawRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity {

    private EditText amountInput;
    private Button confirmButton;
    private TextView balanceText;
    private ApiService apiService;
    private String userEmail;
    private double currentBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        initializeViews();
        apiService = RetrofitClient.getApiService();

        getUserData();
        setupConfirmButton();
    }

    private void initializeViews() {
        amountInput = findViewById(R.id.withdraw_amount_input);
        confirmButton = findViewById(R.id.withdraw_confirm_button);
        balanceText = findViewById(R.id.withdraw_result_message);
    }

    private void getUserData() {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);
        currentBalance = prefs.getFloat("balance", 0.0f);

        if (userEmail != null) {
            updateBalanceText();
        } else {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupConfirmButton() {
        confirmButton.setOnClickListener(v -> performWithdraw());
    }

    private void performWithdraw() {
        String amountStr = amountInput.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount > currentBalance) {
            Toast.makeText(this, "Insufficient funds", Toast.LENGTH_SHORT).show();
            return;
        }

        useBackendWithdraw(amount);
    }

    private void useBackendWithdraw(double amount) {
        WithdrawRequest withdrawRequest = new WithdrawRequest(userEmail, amount);

        Call<User> call = apiService.withdraw(withdrawRequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    currentBalance = user.getBalance();

                    updateUserData(user);
                    updateBalanceText();
                    amountInput.setText("");

                    Toast.makeText(WithdrawActivity.this,
                            "Withdrawn " + String.format("%.2f", amount) + " PLN",
                            Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(WithdrawActivity.this, DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }, 1200);

                } else {
                    Toast.makeText(WithdrawActivity.this,
                            "Server error",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(WithdrawActivity.this,
                        "Connection error",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUserData(User user) {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("balance", user.getBalance() != null ? user.getBalance().floatValue() : 0f);
        editor.apply();
    }

    private void updateBalanceText() {
        balanceText.setText(String.format("Balance: %.2f PLN", currentBalance));
    }
}