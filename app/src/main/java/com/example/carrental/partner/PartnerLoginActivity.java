package com.example.carrental.partner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carrental.R;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.models.Showroom;
import com.example.carrental.models.ShowroomLoginRequest;
import com.example.carrental.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartnerLoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_login);

        sessionManager = new SessionManager(this);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        
        etUsername = findViewById(R.id.et_sr_username);
        etPassword = findViewById(R.id.et_sr_password);

        findViewById(R.id.btn_login_showroom).setOnClickListener(v -> loginPartner());
    }

    private void loginPartner() {
        String name = etUsername.getText().toString().trim();
        String contact = etPassword.getText().toString().trim();

        if (name.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please enter both Showroom Name and Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }

        ShowroomLoginRequest request = new ShowroomLoginRequest(name, contact);

        RetrofitClient.getCarApiService().loginShowroom(request).enqueue(new Callback<Showroom>() {
            @Override
            public void onResponse(Call<Showroom> call, Response<Showroom> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionManager.setPartner(true);
                    sessionManager.setShowroomId(response.body().getId()); // Updates ID and login time!
                    Toast.makeText(PartnerLoginActivity.this, "Welcome back, " + response.body().getName(), Toast.LENGTH_SHORT).show();
                    
                    // Navigate to Dashboard and clear login from backstack
                    Intent intent = new Intent(PartnerLoginActivity.this, PartnerDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PartnerLoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Showroom> call, Throwable t) {
                Toast.makeText(PartnerLoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
