package com.example.bankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bankapp.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class CardSettingsActivity extends AppCompatActivity {

    private TextInputEditText dailyLimitInput, transactionLimitInput;
    private SwitchMaterial switchOnlinePayments, switchAtmWithdrawals;
    private Button btnSaveSettings;
    private Long cardId;

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String BASE_URL = "http://10.0.2.2:8080/api/virtual-cards/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_settings);

        cardId = getIntent().getLongExtra("cardId", -1);
        if (cardId == -1) {
            Toast.makeText(this, "Card ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadCurrentSettings();
        setupClickListeners();
    }

    private void initViews() {
        dailyLimitInput = findViewById(R.id.daily_limit_input);
        transactionLimitInput = findViewById(R.id.transaction_limit_input);
        switchOnlinePayments = findViewById(R.id.switch_online_payments);
        switchAtmWithdrawals = findViewById(R.id.switch_atm_withdrawals);
        btnSaveSettings = findViewById(R.id.btn_save_settings);
    }

    private void loadCurrentSettings() {
        String url = BASE_URL + cardId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(CardSettingsActivity.this, "Failed to load settings", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);

                        runOnUiThread(() -> {
                            try {
                                if (json.has("dailyLimit") && !json.isNull("dailyLimit")) {
                                    dailyLimitInput.setText(String.valueOf(json.getLong("dailyLimit")));
                                }
                                if (json.has("transactionLimit") && !json.isNull("transactionLimit")) {
                                    transactionLimitInput.setText(String.valueOf(json.getLong("transactionLimit")));
                                }
                                if (json.has("onlinePaymentsEnabled")) {
                                    switchOnlinePayments.setChecked(json.getBoolean("onlinePaymentsEnabled"));
                                }
                                if (json.has("atmWithdrawalsEnabled")) {
                                    switchAtmWithdrawals.setChecked(json.getBoolean("atmWithdrawalsEnabled"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setupClickListeners() {
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void saveSettings() {
        String dailyLimitStr = dailyLimitInput.getText().toString().trim();
        String transactionLimitStr = transactionLimitInput.getText().toString().trim();

        if (dailyLimitStr.isEmpty()) {
            dailyLimitInput.setError("Enter daily limit");
            return;
        }

        if (transactionLimitStr.isEmpty()) {
            transactionLimitInput.setError("Enter transaction limit");
            return;
        }

        try {
            long dailyLimit = Long.parseLong(dailyLimitStr);
            long transactionLimit = Long.parseLong(transactionLimitStr);

            if (dailyLimit <= 0) {
                dailyLimitInput.setError("Limit must be greater than 0");
                return;
            }

            if (transactionLimit <= 0) {
                transactionLimitInput.setError("Limit must be greater than 0");
                return;
            }

            if (transactionLimit > dailyLimit) {
                transactionLimitInput.setError("Cannot exceed daily limit");
                return;
            }

            sendSettingsToBackend(dailyLimit, transactionLimit);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSettingsToBackend(double dailyLimit, double transactionLimit) {
        try {
            JSONObject json = new JSONObject();
            json.put("dailyLimit", dailyLimit);
            json.put("transactionLimit", transactionLimit);
            json.put("onlinePaymentsEnabled", switchOnlinePayments.isChecked());
            json.put("atmWithdrawalsEnabled", switchAtmWithdrawals.isChecked());

            String url = BASE_URL + cardId + "/settings";
            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(CardSettingsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(CardSettingsActivity.this, "Settings saved successfully", Toast.LENGTH_SHORT).show();

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("settingsChanged", true);
                            setResult(RESULT_OK, resultIntent);

                            finish();
                        } else {
                            Toast.makeText(CardSettingsActivity.this, "Failed to save settings", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }
}