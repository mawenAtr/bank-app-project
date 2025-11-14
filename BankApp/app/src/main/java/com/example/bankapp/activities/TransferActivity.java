package com.example.bankapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bankapp.R;
import com.example.bankapp.api.ApiService;
import com.example.bankapp.api.RetrofitClient;
import com.example.bankapp.models.TransferRequest;
import com.example.bankapp.models.TransferResponse;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferActivity extends AppCompatActivity {

    private TextInputEditText etReceiverName, etAccountNumber, etStreet, etTransferTitle, etAmount;
    private Button btnSendTransfer;

    private ApiService apiService;
    private String userAccountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        initializeViews();
        apiService = RetrofitClient.getApiService();

        getUserData();
        setupButtonListeners();
    }

    private void initializeViews() {
        etReceiverName = findViewById(R.id.etReceiverName);
        etAccountNumber = findViewById(R.id.etAccountNumber);
        etStreet = findViewById(R.id.etStreet);
        etTransferTitle = findViewById(R.id.etTransferTitle);
        etAmount = findViewById(R.id.etAmount);
        btnSendTransfer = findViewById(R.id.btnSendTransfer);
    }

    private void getUserData() {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        userAccountNumber = prefs.getString("account_number", null);

        if (userAccountNumber == null) {
            Toast.makeText(this, "Error: Account number not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupButtonListeners() {
        btnSendTransfer.setOnClickListener(v -> validateAndSendTransfer());
    }

    private void validateAndSendTransfer() {
        String receiverName = etReceiverName.getText().toString().trim();
        String receiverAccount = etAccountNumber.getText().toString().trim();
        String address = etStreet.getText().toString().trim();
        String title = etTransferTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(receiverName)) {
            etReceiverName.setError("Enter recipient name");
            return;
        }

        if (TextUtils.isEmpty(receiverAccount)) {
            etAccountNumber.setError("Enter recipient account number");
            return;
        }

        if (TextUtils.isEmpty(title)) {
            etTransferTitle.setError("Enter transfer title");
            return;
        }

        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Enter transfer amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                etAmount.setError("Amount must be greater than 0");
                return;
            }
            if (amount > 100000) {
                etAmount.setError("Maximum transfer amount is 100,000 PLN");
                return;
            }
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount format");
            return;
        }

        if (receiverAccount.equals(userAccountNumber)) {
            etAccountNumber.setError("You cannot send transfer to your own account");
            return;
        }

        sendTransfer(receiverName, receiverAccount, address, title, amount);
    }

    private void sendTransfer(String receiverName, String receiverAccount, String address, String title, double amount) {
        btnSendTransfer.setEnabled(false);
        btnSendTransfer.setText("Sending...");

        receiverAccount = receiverAccount.trim().replace(" ", "");
        if (!receiverAccount.startsWith("PL")) {
            receiverAccount = "PL" + receiverAccount;
        }

        TransferRequest transferRequest = new TransferRequest(
                userAccountNumber,
                receiverAccount,
                amount,
                title,
                receiverName,
                address
        );

        Call<TransferResponse> call = apiService.executeTransfer(transferRequest);
        call.enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(Call<TransferResponse> call, Response<TransferResponse> response) {
                btnSendTransfer.setEnabled(true);
                btnSendTransfer.setText("Send Transfer");

                if (response.isSuccessful() && response.body() != null) {
                    TransferResponse transferResponse = response.body();
                    handleTransferSuccess(transferResponse);
                } else {
                    handleTransferError("Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TransferResponse> call, Throwable t) {
                btnSendTransfer.setEnabled(true);
                btnSendTransfer.setText("Send Transfer");
                handleTransferError("Connection error: " + t.getMessage());
            }
        });
    }

    private void handleTransferSuccess(TransferResponse response) {
        updateUserBalance(response.getNewBalance());

        String message = String.format("Transfer sent successfully!\nAmount: %.2f PLN", response.getAmount());
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("NEW_BALANCE", response.getNewBalance());
        startActivity(intent);
        finish();
    }

    private void handleTransferError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void updateUserBalance(Double newBalance) {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("balance", newBalance != null ? newBalance.floatValue() : 0f);
        editor.apply();
    }
}