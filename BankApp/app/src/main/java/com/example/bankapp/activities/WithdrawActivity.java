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
    private TextView currencySymbolText;
    private TextView currencySymbolEdit;
    private ApiService apiService;
    private String userEmail;
    private String currentCurrency = "PLN";
    private double currentBalancePln = 0.0;
    private double currentBalanceEur = 0.0;
    private double currentBalanceUsd = 0.0;

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
        currencySymbolText = findViewById(R.id.currency_symbol_text);
        currencySymbolEdit = findViewById(R.id.currency_symbol_edit);
    }

    private void getUserData() {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);
        currentCurrency = prefs.getString("preferred_currency", "PLN");

        currentBalancePln = prefs.getFloat("balance_pln", 0.0f);
        currentBalanceEur = prefs.getFloat("balance_eur", 0.0f);
        currentBalanceUsd = prefs.getFloat("balance_usd", 0.0f);

        if (userEmail != null) {
            updateCurrencyDisplay();
            updateBalanceText();
        } else {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateCurrencyDisplay() {
        String symbol = getCurrencySymbol(currentCurrency);
        if (currencySymbolText != null) {
            currencySymbolText.setText(currentCurrency);
        }
        if (currencySymbolEdit != null) {
            currencySymbolEdit.setText(symbol);
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

        double currentBalance = getCurrentBalance();
        if (amount > currentBalance) {
            Toast.makeText(this, "Insufficient funds", Toast.LENGTH_SHORT).show();
            return;
        }

        useBackendWithdraw(amount);
    }

    private double getCurrentBalance() {
        switch (currentCurrency) {
            case "EUR":
                return currentBalanceEur;
            case "USD":
                return currentBalanceUsd;
            case "PLN":
            default:
                return currentBalancePln;
        }
    }

    private void useBackendWithdraw(double amount) {
        WithdrawRequest withdrawRequest = new WithdrawRequest(userEmail, amount);
        withdrawRequest.setCurrency(currentCurrency);

        Call<User> call;
        switch (currentCurrency) {
            case "EUR":
                call = apiService.withdrawEur(withdrawRequest);
                break;
            case "USD":
                call = apiService.withdrawUsd(withdrawRequest);
                break;
            case "PLN":
            default:
                call = apiService.withdrawPln(withdrawRequest);
                break;
        }

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    switch (currentCurrency) {
                        case "EUR":
                            currentBalanceEur = user.getBalanceEur() != null ?
                                    user.getBalanceEur().doubleValue() : 0.0;
                            break;
                        case "USD":
                            currentBalanceUsd = user.getBalanceUsd() != null ?
                                    user.getBalanceUsd().doubleValue() : 0.0;
                            break;
                        case "PLN":
                        default:
                            currentBalancePln = user.getBalancePln() != null ?
                                    user.getBalancePln().doubleValue() : 0.0;
                            break;
                    }

                    updateUserData(user);
                    updateBalanceText();
                    amountInput.setText("");

                    String symbol = getCurrencySymbol(currentCurrency);
                    Toast.makeText(WithdrawActivity.this,
                            "Withdrawn " + String.format("%.2f", amount) + " " + symbol,
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

        if (user.getBalancePln() != null) {
            editor.putFloat("balance_pln", user.getBalancePln().floatValue());
        }
        if (user.getBalanceEur() != null) {
            editor.putFloat("balance_eur", user.getBalanceEur().floatValue());
        }
        if (user.getBalanceUsd() != null) {
            editor.putFloat("balance_usd", user.getBalanceUsd().floatValue());
        }

        editor.apply();
    }

    private void updateBalanceText() {
        double currentBalance = getCurrentBalance();
        String symbol = getCurrencySymbol(currentCurrency);
        balanceText.setText(String.format("Balance: %.2f %s", currentBalance, symbol));
    }

    private String getCurrencySymbol(String currency) {
        switch (currency) {
            case "PLN": return "zł";
            case "EUR": return "€";
            case "USD": return "$";
            default: return currency;
        }
    }
}