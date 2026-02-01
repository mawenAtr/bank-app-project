package com.example.bankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bankapp.R;
import com.google.android.material.card.MaterialCardView;

public class ATMOperationActivity extends AppCompatActivity {

    private MaterialCardView depositCard;
    private MaterialCardView withdrawCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_operation);

        setupButtons();
    }

    private void setupButtons() {
        depositCard = findViewById(R.id.deposit_card);
        withdrawCard = findViewById(R.id.withdraw_card);

        depositCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, DepositActivity.class);
            startActivity(intent);
        });

        withdrawCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, WithdrawActivity.class);
            startActivity(intent);
        });
    }

}