package com.example.bankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankapp.R;
import com.example.bankapp.api.ApiService;
import com.example.bankapp.api.RetrofitClient;
import com.example.bankapp.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, emailInput, phoneInput, addressInput, peselInput, dobInput, cityInput, zipCodeInput, passwordInput;
    private Button createAccountButton;
    private TextView loginLink;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initializeViews();
        apiService = RetrofitClient.getApiService();

        createAccountButton.setOnClickListener(v -> createAccount());
        loginLink.setOnClickListener(v -> navigateToLogin());
    }

    private void initializeViews() {
        firstNameInput = findViewById(R.id.input_first_name);
        lastNameInput = findViewById(R.id.input_last_name);
        emailInput = findViewById(R.id.input_email);
        phoneInput = findViewById(R.id.input_phone);
        addressInput = findViewById(R.id.input_address);
        peselInput = findViewById(R.id.input_pesel);
        dobInput = findViewById(R.id.input_dob);
        cityInput = findViewById(R.id.input_city);
        zipCodeInput = findViewById(R.id.input_zip_code);
        createAccountButton = findViewById(R.id.register_button);
        passwordInput = findViewById(R.id.input_password);
        loginLink = findViewById(R.id.login_link);

        setInputFilters();
    }

    private void createAccount() {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().replaceAll("[^\\d]", "");
        String address = addressInput.getText().toString().trim();
        String pesel = peselInput.getText().toString().replaceAll("[^\\d]", "");
        String dobStr = dobInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String zipCode = zipCodeInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (zipCode.isEmpty() || zipCode.equals("--") || zipCode.replaceAll("[^\\d]", "").isEmpty()) {
            zipCode = null;
        }

        if (!validateInputs(firstName, lastName, email, phone, pesel, dobStr, password)) {
            return;
        }

        String accountNumber = generateBankAccountNumber();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setAddress(address);
        user.setPesel(pesel);
        user.setDateOfBirth(dobStr);
        user.setCity(city);
        user.setZipCode(zipCode);
        user.setAccountNumber(accountNumber);
        user.setPassword(password);

        sendUserToBackend(user);
    }

    private boolean validateInputs(String firstName, String lastName, String email,
                                   String phone, String pesel, String dobStr,
                                   String password) {

        if (firstName.isEmpty()) {
            Toast.makeText(this, "First name is required", Toast.LENGTH_SHORT).show();
            firstNameInput.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            Toast.makeText(this, "Last name is required", Toast.LENGTH_SHORT).show();
            lastNameInput.requestFocus();
            return false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            emailInput.requestFocus();
            return false;
        }

        if (phone.length() != 9) {
            Toast.makeText(this, "Phone number must be exactly 9 digits", Toast.LENGTH_SHORT).show();
            phoneInput.requestFocus();
            return false;
        }

        if (pesel.length() != 11) {
            Toast.makeText(this, "PESEL must be exactly 11 digits", Toast.LENGTH_SHORT).show();
            peselInput.requestFocus();
            return false;
        }

        if (!isValidDate(dobStr)) {
            Toast.makeText(this, "Invalid date of birth", Toast.LENGTH_SHORT).show();
            dobInput.requestFocus();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            passwordInput.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            Date date = sdf.parse(dateStr);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }

    private void setInputFilters() {
        phoneInput.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(9),
                new DigitsKeyListener(Boolean.FALSE, Boolean.FALSE)
        });

        peselInput.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(11),
                new DigitsKeyListener(Boolean.FALSE, Boolean.FALSE)
        });

        setupDateInput();
        setupZipCodeInput();
    }

    private void setupDateInput() {
        dobInput.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String text = s.toString().replaceAll("[^\\d]", "");
                if (text.length() >= 4) {
                    text = text.substring(0, 4) + "-" + text.substring(4);
                }
                if (text.length() >= 7) {
                    text = text.substring(0, 7) + "-" + text.substring(7);
                }
                if (text.length() > 10) {
                    text = text.substring(0, 10);
                }

                s.replace(0, s.length(), text);
                isFormatting = false;
            }
        });
    }

    private void setupZipCodeInput() {
        zipCodeInput.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String text = s.toString().replaceAll("[^\\d]", "");
                if (text.length() >= 2) {
                    text = text.substring(0, 2) + "-" + text.substring(2);
                }
                if (text.length() > 6) {
                    text = text.substring(0, 6);
                }

                s.replace(0, s.length(), text);
                isFormatting = false;
            }
        });
    }

    private void sendUserToBackend(User user) {
        Call<User> call = apiService.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateAccountActivity.this, "Account created successfully!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                    intent.putExtra("email", user.getEmail());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Error creating account", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(CreateAccountActivity.this, "Connection error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private String generateBankAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("PL");
        for (int i = 0; i < 24; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}