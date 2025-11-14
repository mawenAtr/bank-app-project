package com.example.bankapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bankapp.R;
import com.example.bankapp.api.ApiService;
import com.example.bankapp.api.RetrofitClient;
import com.example.bankapp.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private TextView accountNumberText;
    private TextView balanceText;
    private ImageButton toggleBalanceButton;
    private boolean isBalanceVisible = false;

    private ApiService apiService;
    private String userEmail;
    private String accountNumber;
    private Double currentBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        apiService = RetrofitClient.getApiService();
        initializeViews();

        if (getIntent().hasExtra("NEW_BALANCE")) {
            double newBalance = getIntent().getDoubleExtra("NEW_BALANCE", 0);
            updateUserBalance(newBalance);
            refreshBalanceDisplay(newBalance);
            loadUserDataWithoutBalance();
        } else {
            checkSharedPreferences();
        }

        setupButtonListeners();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (intent.hasExtra("NEW_BALANCE")) {
            double newBalance = intent.getDoubleExtra("NEW_BALANCE", 0.0);
            updateUserBalance(newBalance);
            refreshBalanceDisplay(newBalance);
        }
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcome_text);
        accountNumberText = findViewById(R.id.account_number_text);
        balanceText = findViewById(R.id.balance_text);
        toggleBalanceButton = findViewById(R.id.toggle_balance_button);

        hideBalance();
        toggleBalanceButton.setOnClickListener(v -> toggleBalanceVisibility());
    }

    private void checkSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        userEmail = prefs.getString("user_email", null);
        accountNumber = prefs.getString("account_number", null);

        if (isLoggedIn && userEmail != null) {
            loadUserData();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void loadUserData() {
        Call<User> call = apiService.getUserByEmail(userEmail);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    displayUserData(user);
                } else {
                    displayDataFromPreferences();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                displayDataFromPreferences();
            }
        });
    }

    private void loadUserDataWithoutBalance() {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        String firstName = prefs.getString("first_name", null);
        String accountNum = prefs.getString("account_number", null);
        userEmail = prefs.getString("user_email", null);

        if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName + "!");
        } else {
            welcomeText.setText("Welcome!");
        }

        if (accountNum != null) {
            String maskedAccount = maskAccountNumberLast4(accountNum);
            accountNumberText.setText("Account: " + maskedAccount);
            accountNumber = accountNum;
        }
    }

    private void displayUserData(User user) {
        String firstName = user.getFirstName();
        String accountNum = user.getAccountNumber();
        Double balance = user.getBalance();

        if (balance == null) {
            balance = 0.0;
        }
        currentBalance = balance;

        if (firstName != null && !firstName.trim().isEmpty()) {
            String formattedName = firstName.trim();
            welcomeText.setText("Welcome, " + formattedName + "!");
        } else {
            welcomeText.setText("Welcome!");
        }

        if (accountNum != null) {
            String maskedAccount = maskAccountNumberLast4(accountNum);
            accountNumberText.setText("Account: " + maskedAccount);
            accountNumber = accountNum;
        }

        if (isBalanceVisible) {
            showBalance();
        } else {
            hideBalance();
        }

        saveUserDataToPreferences(user);
    }

    private void displayDataFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        String firstName = prefs.getString("first_name", null);
        String accountNum = prefs.getString("account_number", null);
        float balance = prefs.getFloat("balance", 0.0f);

        currentBalance = (double) balance;

        if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName + "!");
        } else {
            welcomeText.setText("Welcome!");
        }

        if (accountNum != null) {
            String maskedAccount = maskAccountNumberLast4(accountNum);
            accountNumberText.setText("Account: " + maskedAccount);
            accountNumber = accountNum;
        }

        if (isBalanceVisible) {
            showBalance();
        } else {
            hideBalance();
        }
    }

    private void refreshBalanceDisplay(double balance) {
        currentBalance = balance;
        if (isBalanceVisible) {
            showBalance();
        } else {
            hideBalance();
        }
    }

    private void updateUserBalance(double newBalance) {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("balance", (float) newBalance);
        editor.apply();
    }

    private void setupButtonListeners() {
    }

    private String maskAccountNumberLast4(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "••••";
        }
        String last4Digits = accountNumber.substring(accountNumber.length() - 4);
        return "••••" + last4Digits;
    }

    private void toggleBalanceVisibility() {
        if (isBalanceVisible) {
            hideBalance();
        } else {
            showBalance();
        }
        isBalanceVisible = !isBalanceVisible;
    }

    private void showBalance() {
        String balanceFormatted = String.format("%,.2f PLN", currentBalance);
        balanceText.setText("Balance: " + balanceFormatted);
        toggleBalanceButton.setImageResource(R.drawable.ic_visibility);
    }

    private void hideBalance() {
        balanceText.setText("Balance: ••••• PLN");
        toggleBalanceButton.setImageResource(R.drawable.ic_visibility_off);
    }

    private void saveUserDataToPreferences(User user) {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_email", user.getEmail());
        editor.putString("first_name", user.getFirstName());
        editor.putString("account_number", user.getAccountNumber());

        Double balance = user.getBalance();
        if (balance == null) {
            balance = 0.0;
        }
        editor.putFloat("balance", balance.floatValue());
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    public void onTransferClick(View view) {
        Intent intent = new Intent(DashboardActivity.this, TransferActivity.class);
        intent.putExtra("account_number", accountNumber);
        startActivity(intent);
    }

    public void onHistoryClick(View view) {
        Intent intent = new Intent(DashboardActivity.this, HistoryActivity.class);
        intent.putExtra("account_number", accountNumber);
        startActivity(intent);
    }

    public void onAtmClick(View view) {
        Intent intent = new Intent(DashboardActivity.this, ATMOperationActivity.class);
        intent.putExtra("account_number", accountNumber);
        startActivity(intent);
    }

    public void onSettingsClick(View view) {
        Intent intent = new Intent(DashboardActivity.this, AccountSettingsActivity.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

    public void onLogoutClick(View view) {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserData();
    }

    private void refreshUserData() {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        float updatedBalance = prefs.getFloat("balance", 0.0f);

        if (Math.abs(currentBalance - updatedBalance) > 0.01) {
            currentBalance = (double) updatedBalance;
            if (isBalanceVisible) {
                showBalance();
            } else {
                hideBalance();
            }
        }
    }

}