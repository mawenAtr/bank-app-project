package com.example.bankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bankapp.R;
import com.google.android.material.textfield.TextInputEditText;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class ChangePinActivity extends AppCompatActivity {

    private TextInputEditText currentPinInput, newPinInput, confirmPinInput;
    private Button btnChangePin;
    private Long cardId;

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String BASE_URL = "http://10.0.2.2:8080/api/virtual-cards/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        cardId = getIntent().getLongExtra("cardId", -1);
        if (cardId == -1) {
            Toast.makeText(this, "Card ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        currentPinInput = findViewById(R.id.current_pin_input);
        newPinInput = findViewById(R.id.new_pin_input);
        confirmPinInput = findViewById(R.id.confirm_pin_input);
        btnChangePin = findViewById(R.id.btn_change_pin);
    }

    private void setupClickListeners() {
        btnChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePin();
            }
        });
    }

    private void changePin() {
        String currentPin = currentPinInput.getText().toString().trim();
        String newPin = newPinInput.getText().toString().trim();
        String confirmPin = confirmPinInput.getText().toString().trim();

        if (currentPin.isEmpty()) {
            currentPinInput.setError("Enter current PIN");
            return;
        }

        if (newPin.isEmpty()) {
            newPinInput.setError("Enter new PIN");
            return;
        }

        if (newPin.length() != 4) {
            newPinInput.setError("PIN must be 4 digits");
            return;
        }

        if (!newPin.equals(confirmPin)) {
            confirmPinInput.setError("PINs don't match");
            return;
        }

        sendPinChangeRequest(currentPin, newPin, confirmPin);
    }

    private void sendPinChangeRequest(String currentPin, String newPin, String confirmPin) {
        try {
            JSONObject json = new JSONObject();
            json.put("currentPin", currentPin);
            json.put("newPin", newPin);
            json.put("confirmPin", confirmPin);

            String url = BASE_URL + cardId + "/pin";
            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(ChangePinActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(ChangePinActivity.this, "PIN changed successfully", Toast.LENGTH_SHORT).show();

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("pinChanged", true);
                            setResult(RESULT_OK, resultIntent);

                            finish();
                        } else {
                            try {
                                String errorBody = response.body().string();
                                if (errorBody.contains("Current PIN is incorrect")) {
                                    currentPinInput.setError("Incorrect current PIN");
                                } else {
                                    Toast.makeText(ChangePinActivity.this, "Failed to change PIN", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(ChangePinActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }
}