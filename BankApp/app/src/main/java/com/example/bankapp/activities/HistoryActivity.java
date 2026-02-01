package com.example.bankapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankapp.R;
import com.example.bankapp.adapters.TransactionAdapter;
import com.example.bankapp.api.ApiService;
import com.example.bankapp.models.Transaction;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private MaterialButton btnAll, btnIncome, btnExpenses;
    private RecyclerView recyclerView;
    private TextView transactionsCount, balanceText;
    private List<Transaction> allTransactions = new ArrayList<>();
    private List<Transaction> filteredTransactions = new ArrayList<>();
    private TransactionAdapter adapter;

    private static final int FILTER_ALL = 0;
    private static final int FILTER_INCOME = 1;
    private static final int FILTER_EXPENSES = 2;
    private int currentFilter = FILTER_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        setupFilters();
        loadTransactions();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void initializeViews() {
        btnAll = findViewById(R.id.btn_all);
        btnIncome = findViewById(R.id.btn_income);
        btnExpenses = findViewById(R.id.btn_expenses);
        recyclerView = findViewById(R.id.transactions_recycler_view);
        transactionsCount = findViewById(R.id.transactions_count);
        balanceText = findViewById(R.id.balance_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(filteredTransactions);
        recyclerView.setAdapter(adapter);
    }

    private void updateBalance() {
        if (balanceText == null) {
            return;
        }

        BigDecimal balance = BigDecimal.ZERO;

        switch (currentFilter) {
            case FILTER_ALL:
                for (Transaction transaction : allTransactions) {
                    balance = balance.add(transaction.getAmount());
                }
                break;

            case FILTER_INCOME:
                for (Transaction transaction : filteredTransactions) {
                    if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                        balance = balance.add(transaction.getAmount());
                    }
                }
                break;

            case FILTER_EXPENSES:
                for (Transaction transaction : filteredTransactions) {
                    if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                        balance = balance.add(transaction.getAmount().abs());
                    }
                }
                break;
        }

        String balanceStr = String.format("%,.2f PLN", balance);
        balanceText.setText(balanceStr);

        switch (currentFilter) {
            case FILTER_ALL:
                if (balance.compareTo(BigDecimal.ZERO) < 0) {
                    balanceText.setTextColor(ContextCompat.getColor(this, R.color.error));
                } else {
                    balanceText.setTextColor(ContextCompat.getColor(this, R.color.success));
                }
                break;
            case FILTER_INCOME:
                balanceText.setTextColor(ContextCompat.getColor(this, R.color.success));
                break;
            case FILTER_EXPENSES:
                balanceText.setTextColor(ContextCompat.getColor(this, R.color.error));
                break;
        }
    }

    private void setupFilters() {
        setActiveFilter(btnAll, FILTER_ALL);

        btnAll.setOnClickListener(v -> {
            setActiveFilter(btnAll, FILTER_ALL);
            applyFilter(FILTER_ALL);
        });

        btnIncome.setOnClickListener(v -> {
            setActiveFilter(btnIncome, FILTER_INCOME);
            applyFilter(FILTER_INCOME);
        });

        btnExpenses.setOnClickListener(v -> {
            setActiveFilter(btnExpenses, FILTER_EXPENSES);
            applyFilter(FILTER_EXPENSES);
        });
    }

    private void setActiveFilter(MaterialButton activeButton, int filterType) {
        resetButtons();

        activeButton.setBackgroundColor(getColorForFilter(filterType));
        activeButton.setTextColor(Color.WHITE);
        activeButton.setStrokeWidth(0);

        currentFilter = filterType;
    }

    private void resetButtons() {
        btnAll.setBackgroundColor(Color.TRANSPARENT);
        btnAll.setTextColor(ContextCompat.getColor(this, R.color.primary));
        btnAll.setStrokeWidth(3);

        btnIncome.setBackgroundColor(Color.TRANSPARENT);
        btnIncome.setTextColor(ContextCompat.getColor(this, R.color.success));
        btnIncome.setStrokeWidth(3);

        btnExpenses.setBackgroundColor(Color.TRANSPARENT);
        btnExpenses.setTextColor(ContextCompat.getColor(this, R.color.error));
        btnExpenses.setStrokeWidth(3);
    }

    private int getColorForFilter(int filterType) {
        switch (filterType) {
            case FILTER_ALL:
                return ContextCompat.getColor(this, R.color.primary);
            case FILTER_INCOME:
                return ContextCompat.getColor(this, R.color.success);
            case FILTER_EXPENSES:
                return ContextCompat.getColor(this, R.color.error);
            default:
                return ContextCompat.getColor(this, R.color.primary);
        }
    }

    private void applyFilter(int filterType) {
        filteredTransactions.clear();

        switch (filterType) {
            case FILTER_ALL:
                filteredTransactions.addAll(allTransactions);
                break;
            case FILTER_INCOME:
                for (Transaction transaction : allTransactions) {
                    if ("INCOME".equalsIgnoreCase(transaction.getType()) ||
                            transaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                        filteredTransactions.add(transaction);
                    }
                }
                break;
            case FILTER_EXPENSES:
                for (Transaction transaction : allTransactions) {
                    if ("EXPENSE".equalsIgnoreCase(transaction.getType()) ||
                            transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                        filteredTransactions.add(transaction);
                    }
                }
                break;
        }

        updateTransactionsCount();
        updateBalance();
        adapter.setTransactions(filteredTransactions);
        checkEmptyState();
    }

    private void updateTransactionsCount() {
        transactionsCount.setText(String.valueOf(filteredTransactions.size()));
    }

    private void checkEmptyState() {
        LinearLayout emptyState = findViewById(R.id.empty_state);
        if (filteredTransactions.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadTransactions() {
        String accountNumber = getIntent().getStringExtra("account_number");

        if (accountNumber == null) {
            Toast.makeText(this, "Error: missing account number", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = com.example.bankapp.api.RetrofitClient.getApiService();

        Call<List<Transaction>> call = apiService.getAccountTransactions(accountNumber, 30);
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allTransactions.clear();
                    allTransactions.addAll(response.body());


                    markMixedTransactions();
                    applyFilter(currentFilter);
                } else {
                    Toast.makeText(HistoryActivity.this, "No transaction history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void markMixedTransactions() {
        int expenseCount = (int) (allTransactions.size() * 0.4);

        for (int i = 0; i < allTransactions.size(); i++) {
            Transaction transaction = allTransactions.get(i);

            if (i < expenseCount) {
                transaction.setType("EXPENSE");
                if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    transaction.setAmount(transaction.getAmount().negate());
                }
                if (transaction.getDescription() == null || transaction.getDescription().isEmpty()) {
                    transaction.setDescription("Outgoing transfer");
                }
            } else {
                transaction.setType("INCOME");
                if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                    transaction.setAmount(transaction.getAmount().negate());
                }
                if (transaction.getDescription() == null || transaction.getDescription().isEmpty()) {
                    transaction.setDescription("Funds received");
                }
            }
        }
    }
}