package com.example.carrental.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrental.R;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.models.User;
import com.example.carrental.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword;
    private MaterialButton btnRegister;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);

        etName = findViewById(R.id.et_register_name);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> registerUser());

        findViewById(R.id.tv_go_to_login).setOnClickListener(v -> {
            finish(); // Go back to login
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 4) {
            Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Creating Account...");

        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("name", name);
        userDetails.put("email", email);
        userDetails.put("password", password);

        RetrofitClient.getCarApiService().register(userDetails).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    sessionManager.createLoginSession(
                            user.getId() != null ? user.getId() : "new-user",
                            user.getName() != null ? user.getName() : name,
                            user.getEmail() != null ? user.getEmail() : email
                    );
                    Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
