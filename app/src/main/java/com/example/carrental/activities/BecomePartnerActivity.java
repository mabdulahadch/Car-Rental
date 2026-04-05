package com.example.carrental.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrental.R;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.models.Showroom;
import com.example.carrental.models.ShowroomCreateRequest;
import com.example.carrental.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BecomePartnerActivity extends AppCompatActivity {

    private TextInputEditText etName, etContact, etLocation;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_partner);

        sessionManager = new SessionManager(this);

        etName = findViewById(R.id.et_sr_name);
        etContact = findViewById(R.id.et_sr_contact);
        etLocation = findViewById(R.id.et_sr_location);

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
                if(response.isSuccessful()) {
                    sessionManager.setPartner(true);
                    Toast.makeText(BecomePartnerActivity.this, "Success! You are now a partner.", Toast.LENGTH_LONG).show();
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
