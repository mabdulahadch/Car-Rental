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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> loginUser());
        findViewById(R.id.tv_register).setOnClickListener(v -> {
            Toast.makeText(this, "Redirect to Register...", Toast.LENGTH_SHORT).show();
            // Just simulate success
            sessionManager.createLoginSession("test-id", "Guest", etEmail.getText().toString());
            finish();
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();

        if(email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User userReq = new User(email, pass);
        RetrofitClient.getCarApiService().login(userReq).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful() && response.body() != null) {
                    sessionManager.createLoginSession(response.body().getId(), response.body().getName(), response.body().getEmail());
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
