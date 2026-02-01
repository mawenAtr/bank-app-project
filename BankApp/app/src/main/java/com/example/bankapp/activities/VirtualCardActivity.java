package com.example.bankapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bankapp.R;
import com.google.android.material.card.MaterialCardView;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VirtualCardActivity extends AppCompatActivity {

    private MaterialCardView createCardButton, cardContainer;
    private LinearLayout cardContent, cardActionsContainer;
    private ImageButton toggleCardVisibilityButton;
    private Button btnChangePin, btnSettings, btnBlockCard;
    private TextView virtualCardNumber, virtualCardExpiry, virtualCardCvv;

    private String fullCardNumber = "";
    private String fullCvv = "";
    private String expiryDate = "";
    private Long cardId = null;
    private Long userId = 1L;
    private boolean isCardVisible = false;

    private final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://10.0.2.2:8080/api/virtual-cards/";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_card);

        initViews();
        checkExistingCard();
        setupClickListeners();
    }

    private void initViews() {
        createCardButton = findViewById(R.id.create_card_button);
        cardContainer = findViewById(R.id.card_container);
        cardContent = findViewById(R.id.card_content);
        cardActionsContainer = findViewById(R.id.card_actions_container);

        toggleCardVisibilityButton = findViewById(R.id.toggle_card_visibility_button);
        btnChangePin = findViewById(R.id.btn_change_pin);
        btnSettings = findViewById(R.id.btn_settings);
        btnBlockCard = findViewById(R.id.btn_block_card);

        virtualCardNumber = findViewById(R.id.virtual_card_number);
        virtualCardExpiry = findViewById(R.id.virtual_card_expiry);
        virtualCardCvv = findViewById(R.id.virtual_card_cvv);
    }

    private void checkExistingCard() {
        String url = BASE_URL + "user/" + userId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    showCreateCardView();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        cardId = json.getLong("id");
                        fullCardNumber = json.getString("cardNumber");
                        fullCvv = json.getString("cvv");
                        expiryDate = json.getString("expiryDate");

                        runOnUiThread(() -> {
                            showCardView();
                            updateCardDisplay();
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> showCreateCardView());
                    }
                } else {
                    runOnUiThread(() -> showCreateCardView());
                }
            }
        });
    }

    private void showCreateCardView() {
        createCardButton.setVisibility(View.VISIBLE);
        cardContainer.setVisibility(View.GONE);
        cardActionsContainer.setVisibility(View.GONE);
    }

    private void showCardView() {
        createCardButton.setVisibility(View.GONE);
        cardContainer.setVisibility(View.VISIBLE);
        cardActionsContainer.setVisibility(View.VISIBLE);
    }

    private void setupClickListeners() {
        createCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createVirtualCard();
            }
        });

        toggleCardVisibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCardVisibility();
            }
        });

        btnChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VirtualCardActivity.this, ChangePinActivity.class);
                intent.putExtra("cardId", cardId);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VirtualCardActivity.this, CardSettingsActivity.class);
                intent.putExtra("cardId", cardId);
                startActivity(intent);
            }
        });

        btnBlockCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockVirtualCard();
            }
        });
    }

    private void toggleCardVisibility() {
        isCardVisible = !isCardVisible;
        updateCardDisplay();

        if (isCardVisible) {
            toggleCardVisibilityButton.setImageResource(R.drawable.ic_visibility);
        } else {
            toggleCardVisibilityButton.setImageResource(R.drawable.ic_visibility_off);
        }
    }

    private void createVirtualCard() {
        try {
            JSONObject json = new JSONObject();
            json.put("userId", userId);
            json.put("pin", "0000");

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "create")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(VirtualCardActivity.this, "Network error", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            JSONObject json = new JSONObject(responseData);
                            cardId = json.getLong("id");
                            fullCardNumber = json.getString("cardNumber");
                            fullCvv = json.getString("cvv");
                            expiryDate = json.getString("expiryDate");

                            runOnUiThread(() -> {
                                showCardView();
                                updateCardDisplay();
                                toggleCardVisibilityButton.setImageResource(R.drawable.ic_visibility_off);
                                Toast.makeText(VirtualCardActivity.this, "Virtual card created successfully!", Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() ->
                                    Toast.makeText(VirtualCardActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show()
                            );
                        }
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(VirtualCardActivity.this, "Failed to create card", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error creating card", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCardDisplay() {
        virtualCardExpiry.setText(expiryDate);

        if (isCardVisible) {
            String formattedNumber = formatCardNumber(fullCardNumber);
            virtualCardNumber.setText(formattedNumber);
            virtualCardCvv.setText(fullCvv);
        } else {
            String maskedNumber = maskCardNumber(fullCardNumber);
            virtualCardNumber.setText(maskedNumber);
            virtualCardCvv.setText("***");
        }
    }

    private String formatCardNumber(String cardNumber) {
        if (cardNumber.length() != 16) return cardNumber;
        return cardNumber.substring(0, 4) + " " +
                cardNumber.substring(4, 8) + " " +
                cardNumber.substring(8, 12) + " " +
                cardNumber.substring(12, 16);
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() != 16) return "**** **** **** ****";
        String lastFour = cardNumber.substring(12, 16);
        return "**** **** **** " + lastFour;
    }

    private void blockVirtualCard() {
        if (cardId == null) return;

        String url = BASE_URL + cardId + "/block";
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create("", JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(VirtualCardActivity.this, "Network error", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        showCreateCardView();
                        Toast.makeText(VirtualCardActivity.this, "Card blocked successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(VirtualCardActivity.this, "Failed to block card", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkExistingCard();
    }
}