package com.example.bankapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bankapp.R;
import com.example.bankapp.api.ApiService;
import com.example.bankapp.api.RetrofitClient;
import com.example.bankapp.models.User;
import com.google.android.material.card.MaterialCardView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private TextView accountNumberText;
    private TextView balanceText;
    private TextView currencyText;
    private ImageButton toggleBalanceButton;
    private MaterialCardView currencySelectorCard;
    private MaterialCardView currencyPopup;
    private View currencyOptionPln;
    private View currencyOptionEur;
    private View currencyOptionUsd;

    private boolean isBalanceVisible = false;
    private String currentCurrency = "PLN";
    private Map<String, Double> balances = new HashMap<>();

    private ApiService apiService;
    private String userEmail;
    private String accountNumber;

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
        currencyText = findViewById(R.id.currency_text);
        currencySelectorCard = findViewById(R.id.currency_selector_card);
        currencyPopup = findViewById(R.id.currency_popup);
        currencyOptionPln = findViewById(R.id.currency_option_pln);
        currencyOptionEur = findViewById(R.id.currency_option_eur);
        currencyOptionUsd = findViewById(R.id.currency_option_usd);

        hideBalance();
        toggleBalanceButton.setOnClickListener(v -> toggleBalanceVisibility());
    }

    private void setupButtonListeners() {
        currencySelectorCard.setOnClickListener(v -> {
            if (currencyPopup.getVisibility() == View.VISIBLE) {
                currencyPopup.setVisibility(View.GONE);
            } else {
                currencyPopup.setVisibility(View.VISIBLE);
            }
        });

        currencyOptionPln.setOnClickListener(v -> {
            changeCurrency("PLN");
            currencyPopup.setVisibility(View.GONE);
        });

        currencyOptionEur.setOnClickListener(v -> {
            changeCurrency("EUR");
            currencyPopup.setVisibility(View.GONE);
        });

        currencyOptionUsd.setOnClickListener(v -> {
            changeCurrency("USD");
            currencyPopup.setVisibility(View.GONE);
        });
    }

    private void changeCurrency(String currencyCode) {
        currentCurrency = currencyCode;
        currencyText.setText(currencyCode);

        Double balance = balances.getOrDefault(currencyCode, 0.0);
        String symbol = getCurrencySymbol(currencyCode);

        if (isBalanceVisible) {
            balanceText.setText(String.format(Locale.getDefault(), "Balance: %.2f %s", balance, symbol));
        } else {
            balanceText.setText(String.format("Balance: ••••• %s", symbol));
        }

        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("preferred_currency", currencyCode);
        editor.apply();
    }

    private String getCurrencySymbol(String currencyCode) {
        switch (currencyCode) {
            case "PLN": return "zł";
            case "EUR": return "€";
            case "USD": return "$";
            default: return currencyCode;
        }
    }

    private void toggleBalanceVisibility() {
        isBalanceVisible = !isBalanceVisible;

        Double balance = balances.getOrDefault(currentCurrency, 0.0);
        String symbol = getCurrencySymbol(currentCurrency);

        if (isBalanceVisible) {
            balanceText.setText(String.format(Locale.getDefault(), "Balance: %.2f %s", balance, symbol));
            toggleBalanceButton.setImageResource(R.drawable.ic_visibility);
        } else {
            balanceText.setText(String.format("Balance: ••••• %s", symbol));
            toggleBalanceButton.setImageResource(R.drawable.ic_visibility_off);
        }
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

        balances.put("PLN", user.getBalancePln() != null ? user.getBalancePln().doubleValue() : 0.0);
        balances.put("EUR", user.getBalanceEur() != null ? user.getBalanceEur().doubleValue() : 0.0);
        balances.put("USD", user.getBalanceUsd() != null ? user.getBalanceUsd().doubleValue() : 0.0);

        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        String savedCurrency = prefs.getString("preferred_currency", "PLN");
        changeCurrency(savedCurrency);

        saveUserDataToPreferences(user);
    }

    private void displayDataFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        String firstName = prefs.getString("first_name", null);
        String accountNum = prefs.getString("account_number", null);

        balances.put("PLN", (double) prefs.getFloat("balance_pln", 0.0f));
        balances.put("EUR", (double) prefs.getFloat("balance_eur", 0.0f));
        balances.put("USD", (double) prefs.getFloat("balance_usd", 0.0f));

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

        String savedCurrency = prefs.getString("preferred_currency", "PLN");
        changeCurrency(savedCurrency);
    }

    private void refreshBalanceDisplay(double balance) {
        balances.put("PLN", balance);
        if (isBalanceVisible) {
            showBalance();
        } else {
            hideBalance();
        }
    }

    private void updateUserBalance(double newBalance) {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("balance_pln", (float) newBalance);
        editor.apply();
    }

    private String maskAccountNumberLast4(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "••••";
        }
        String last4Digits = accountNumber.substring(accountNumber.length() - 4);
        return "••••" + last4Digits;
    }

    private void showBalance() {
        Double balance = balances.getOrDefault(currentCurrency, 0.0);
        String symbol = getCurrencySymbol(currentCurrency);
        String balanceFormatted = String.format(Locale.getDefault(), "Balance: %.2f %s", balance, symbol);
        balanceText.setText(balanceFormatted);
        toggleBalanceButton.setImageResource(R.drawable.ic_visibility);
    }

    private void hideBalance() {
        String symbol = getCurrencySymbol(currentCurrency);
        balanceText.setText(String.format("Balance: ••••• %s", symbol));
        toggleBalanceButton.setImageResource(R.drawable.ic_visibility_off);
    }

    private void saveUserDataToPreferences(User user) {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_email", user.getEmail());
        editor.putString("first_name", user.getFirstName());
        editor.putString("account_number", user.getAccountNumber());

        if (user.getBalancePln() != null) {
            editor.putFloat("balance_pln", user.getBalancePln().floatValue());
        }
        if (user.getBalanceEur() != null) {
            editor.putFloat("balance_eur", user.getBalanceEur().floatValue());
        }
        if (user.getBalanceUsd() != null) {
            editor.putFloat("balance_usd", user.getBalanceUsd().floatValue());
        }

        editor.putLong("user_id", user.getId());
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

    public void onVirtualCardClick(View view) {
        Intent intent = new Intent(DashboardActivity.this, VirtualCardActivity.class);
        intent.putExtra("account_number", accountNumber);
        startActivity(intent);
    }

    public void onExchangeClick(View view) {
        Intent intent = new Intent(DashboardActivity.this, ExchangeActivity.class);
        intent.putExtra("email", userEmail);
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
        float updatedBalancePln = prefs.getFloat("balance_pln", 0.0f);
        float updatedBalanceEur = prefs.getFloat("balance_eur", 0.0f);
        float updatedBalanceUsd = prefs.getFloat("balance_usd", 0.0f);

        balances.put("PLN", (double) updatedBalancePln);
        balances.put("EUR", (double) updatedBalanceEur);
        balances.put("USD", (double) updatedBalanceUsd);

        if (isBalanceVisible) {
            showBalance();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (currencyPopup.getVisibility() == View.VISIBLE &&
                event.getAction() == MotionEvent.ACTION_DOWN) {

            int[] location = new int[2];
            currencyPopup.getLocationOnScreen(location);
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();

            if (x < location[0] || x > location[0] + currencyPopup.getWidth() ||
                    y < location[1] || y > location[1] + currencyPopup.getHeight()) {
                currencyPopup.setVisibility(View.GONE);
            }
        }
        return super.onTouchEvent(event);
    }
}