package com.example.bankapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bankapp.R;
import com.example.bankapp.api.ApiService;
import com.example.bankapp.api.RetrofitClient;
import com.example.bankapp.models.ExchangeRequest;
import com.example.bankapp.models.ExchangeResponse;
import com.example.bankapp.models.User;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExchangeActivity extends AppCompatActivity {

    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private EditText amountEditText;
    private TextView resultTextView;
    private Button exchangeButton;

    private ApiService apiService;
    private String userEmail;
    private String accountNumber;

    private List<String> currencies = Arrays.asList("PLN", "EUR", "USD");
    private String fromCurrency = "PLN";
    private String toCurrency = "EUR";
    private BigDecimal currentRate = BigDecimal.ONE;
    private BigDecimal commissionRate = new BigDecimal("0.02");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Currency Exchange");
        }

        apiService = RetrofitClient.getApiService();

        userEmail = getIntent().getStringExtra("user_email");
        accountNumber = getIntent().getStringExtra("account_number");

        initializeViews();
        setupSpinners();
        loadUserBalances();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        fromCurrencySpinner = findViewById(R.id.from_currency_spinner);
        toCurrencySpinner = findViewById(R.id.to_currency_spinner);
        amountEditText = findViewById(R.id.amount_edit_text);
        resultTextView = findViewById(R.id.result_text_view);
        exchangeButton = findViewById(R.id.exchange_button);

        exchangeButton.setOnClickListener(v -> performExchange());

        amountEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                calculateExchange();
            }
        });
    }

    private void setupSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setAdapter(adapter);

        fromCurrencySpinner.setSelection(0);
        toCurrencySpinner.setSelection(1);

        fromCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromCurrency = currencies.get(position);
                updateExchangeRates();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        toCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toCurrency = currencies.get(position);
                updateExchangeRates();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadUserBalances() {
        if (userEmail == null) {
            SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
            userEmail = prefs.getString("user_email", null);
        }
        updateExchangeRates();
    }

    private void updateExchangeRates() {
        Call<ExchangeResponse> call = apiService.getExchangeRate(fromCurrency, toCurrency);
        call.enqueue(new Callback<ExchangeResponse>() {
            @Override
            public void onResponse(Call<ExchangeResponse> call, Response<ExchangeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentRate = response.body().getRate();
                    calculateExchange();
                } else {
                    setDefaultRates();
                }
            }

            @Override
            public void onFailure(Call<ExchangeResponse> call, Throwable t) {
                setDefaultRates();
            }
        });
    }

    private void setDefaultRates() {
        if (fromCurrency.equals("PLN") && toCurrency.equals("EUR")) {
            currentRate = new BigDecimal("0.22");
        } else if (fromCurrency.equals("PLN") && toCurrency.equals("USD")) {
            currentRate = new BigDecimal("0.25");
        } else if (fromCurrency.equals("EUR") && toCurrency.equals("PLN")) {
            currentRate = new BigDecimal("4.55");
        } else if (fromCurrency.equals("EUR") && toCurrency.equals("USD")) {
            currentRate = new BigDecimal("1.08");
        } else if (fromCurrency.equals("USD") && toCurrency.equals("PLN")) {
            currentRate = new BigDecimal("4.00");
        } else if (fromCurrency.equals("USD") && toCurrency.equals("EUR")) {
            currentRate = new BigDecimal("0.92");
        } else {
            currentRate = BigDecimal.ONE;
        }
        calculateExchange();
    }

    private void calculateExchange() {
        String amountStr = amountEditText.getText().toString();
        if (amountStr.isEmpty()) {
            resultTextView.setText("0.00");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                resultTextView.setText("0.00");
                return;
            }

            BigDecimal commission = amount.multiply(commissionRate);
            BigDecimal amountAfterCommission = amount.subtract(commission);
            BigDecimal result = amountAfterCommission.multiply(currentRate);

            resultTextView.setText(String.format("%.2f", result));

        } catch (NumberFormatException e) {
            resultTextView.setText("0.00");
        }
    }

    private void performExchange() {
        String amountStr = amountEditText.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            BigDecimal commission = amount.multiply(commissionRate);
            BigDecimal amountAfterCommission = amount.subtract(commission);
            BigDecimal result = amountAfterCommission.multiply(currentRate);

            ExchangeRequest request = new ExchangeRequest();
            request.setUserEmail(userEmail);
            request.setFromCurrency(fromCurrency);
            request.setToCurrency(toCurrency);
            request.setAmount(amount);
            request.setAmountAfterCommission(amountAfterCommission);
            request.setCommission(commission);
            request.setResult(result);
            request.setRate(currentRate);
            request.setAccountNumber(accountNumber);

            Call<ExchangeResponse> call = apiService.performExchange(request);
            call.enqueue(new Callback<ExchangeResponse>() {
                @Override
                public void onResponse(Call<ExchangeResponse> call, Response<ExchangeResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ExchangeResponse exchangeResponse = response.body();
                        if (exchangeResponse.isSuccess()) {
                            Toast.makeText(ExchangeActivity.this,
                                    "Exchange successful!", Toast.LENGTH_SHORT).show();

                            SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();

                            switch (fromCurrency) {
                                case "PLN":
                                    editor.putFloat("balance_pln", exchangeResponse.getNewBalancePln().floatValue());
                                    break;
                                case "EUR":
                                    editor.putFloat("balance_eur", exchangeResponse.getNewBalanceEur().floatValue());
                                    break;
                                case "USD":
                                    editor.putFloat("balance_usd", exchangeResponse.getNewBalanceUsd().floatValue());
                                    break;
                            }

                            editor.apply();
                            finish();

                        } else {
                            Toast.makeText(ExchangeActivity.this,
                                    exchangeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ExchangeActivity.this,
                                "Exchange failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ExchangeResponse> call, Throwable t) {
                    Toast.makeText(ExchangeActivity.this,
                            "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }
}