package com.example.bankapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankapp.R;
import com.example.bankapp.api.ApiService;
import com.example.bankapp.api.RetrofitClient;
import com.example.bankapp.models.User;
import com.example.bankapp.models.PasswordChangeRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountSettingsActivity extends AppCompatActivity {

    private TextView userNameText, userPeselText, userDobText, userAddressText, accountNumberText, userEmailText, userPhoneText;
    private EditText userPasswordEdit;
    private Button saveChangesButton;

    private ApiService apiService;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        apiService = RetrofitClient.getApiService();
        initViews();
        loadUserData();

        saveChangesButton.setOnClickListener(v -> saveChanges());
    }

    private void initViews() {
        userNameText = findViewById(R.id.user_name_text);
        userPeselText = findViewById(R.id.user_pesel_text);
        userDobText = findViewById(R.id.user_dob_text);
        userAddressText = findViewById(R.id.user_address_text);
        accountNumberText = findViewById(R.id.account_number_text);
        userEmailText = findViewById(R.id.user_email_text);
        userPhoneText = findViewById(R.id.user_phone_text);
        userPasswordEdit = findViewById(R.id.user_password_edit);
        saveChangesButton = findViewById(R.id.save_changes_button);
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("bank_app", MODE_PRIVATE);
        userEmail = prefs.getString("user_email", "");

        if (userEmail.isEmpty()) {
            finish();
            return;
        }

        Call<User> call = apiService.getUserByEmail(userEmail);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    displayUserData(user);
                } else {
                    Toast.makeText(AccountSettingsActivity.this,
                            "Cannot load user data from server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AccountSettingsActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayUserData(User user) {
        runOnUiThread(() -> {
            userNameText.setText(user.getFullName());
            userPeselText.setText(user.getPesel() != null ? user.getPesel() : "Not available");

            if (user.getDateOfBirth() != null && !user.getDateOfBirth().isEmpty()) {
                String dateStr = user.getDateOfBirth();

                if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    String[] parts = dateStr.split("-");
                    String formattedDate = parts[2] + "/" + parts[1] + "/" + parts[0];
                    userDobText.setText(formattedDate);
                } else {
                    userDobText.setText(dateStr);
                }
            } else {
                userDobText.setText("Not available");
            }

            String address = user.getAddress();
            String city = user.getCity();
            String zipCode = user.getZipCode();

            if (address != null && city != null && zipCode != null) {
                String fullAddress = address + ", " + zipCode + " " + city;
                userAddressText.setText(fullAddress);
            } else if (address != null) {
                userAddressText.setText(address);
            } else {
                userAddressText.setText("Not available");
            }

            accountNumberText.setText(user.getAccountNumber() != null ? user.getAccountNumber() : "Not available");
            userEmailText.setText(user.getEmail());
            userPhoneText.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "Not available");
        });
    }

    private void saveChanges() {
        String newPassword = userPasswordEdit.getText().toString().trim();

        if (!newPassword.isEmpty()) {
            if (newPassword.length() < 6) {
                userPasswordEdit.setError("Password must be at least 6 characters");
                return;
            }
            changePassword(newPassword);
        } else {
            Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show();
        }
    }

    private void changePassword(String newPassword) {
        PasswordChangeRequest passwordRequest = new PasswordChangeRequest(userEmail, newPassword);

        Call<Void> call = apiService.changePassword(passwordRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    userPasswordEdit.setText("");
                    Toast.makeText(AccountSettingsActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AccountSettingsActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AccountSettingsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}