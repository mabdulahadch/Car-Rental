package com.example.carrental.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrental.R;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.models.Showroom;
import com.example.carrental.models.ShowroomCreateRequest;
import com.example.carrental.partner.PartnerDashboardActivity;
import com.example.carrental.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import android.content.Intent;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.net.Uri;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BecomePartnerActivity extends AppCompatActivity {

    private TextInputEditText etName, etContact, etLocation;
    private SessionManager sessionManager;
    private TextView tvUploadStatus;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    tvUploadStatus.setText("Image selected");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_partner);

        sessionManager = new SessionManager(this);

        etName = findViewById(R.id.et_sr_name);
        etContact = findViewById(R.id.et_sr_contact);
        etLocation = findViewById(R.id.et_sr_location);
        tvUploadStatus = findViewById(R.id.tv_upload_status);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.card_upload_image).setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        findViewById(R.id.btn_register_showroom).setOnClickListener(v -> registerShowroom());
    }

    private void registerShowroom() {
        String name = etName.getText().toString();
        String contact = etContact.getText().toString();
        String location = etLocation.getText().toString();

        if(name.isEmpty() || contact.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = sessionManager.getUserEmail();
        if (email == null) email = "guest@example.com";

        ShowroomCreateRequest req = new ShowroomCreateRequest(name, email, contact, location, new ArrayList<>());
        RetrofitClient.getCarApiService().createShowroom(req).enqueue(new Callback<Showroom>() {
            @Override
            public void onResponse(Call<Showroom> call, Response<Showroom> response) {
                if(response.isSuccessful() && response.body() != null) {
                    sessionManager.setPartner(true);
                    sessionManager.setShowroomId(response.body().getId()); // Note: needs to be added to SessionManager
                    Toast.makeText(BecomePartnerActivity.this, "Success! You are now a partner.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(BecomePartnerActivity.this, PartnerDashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(BecomePartnerActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Showroom> call, Throwable t) {
                Toast.makeText(BecomePartnerActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
