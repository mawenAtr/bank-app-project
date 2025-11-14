package com.example.bankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bankapp.R;

public class ATMOperationActivity extends AppCompatActivity {

    private TextView depositButton;
    private TextView withdrawButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_operation);

        setupButtons();
    }

    private void setupButtons() {
        depositButton = findViewById(R.id.deposit_button);
        withdrawButton = findViewById(R.id.withdraw_button);

        depositButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DepositActivity.class);
            startActivity(intent);
        });

        withdrawButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WithdrawActivity.class);
            startActivity(intent);
        });
    }

}
