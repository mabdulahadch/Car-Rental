package com.example.carrental;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.databinding.ActivityBookingBinding;
import com.example.carrental.models.Booking;
import com.example.carrental.models.Car;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {
    private ActivityBookingBinding binding;
    private Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String carJson = getIntent().getStringExtra("car_json");
        if (carJson != null) {
            car = new Gson().fromJson(carJson, Car.class);
            binding.btnPayNow.setText("$" + (int)car.pricePerDay + " Pay Now");
        }

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnPayNow.setOnClickListener(v -> submitBooking());
    }

    private void submitBooking() {
        String name = binding.etName.getText().toString();
        String email = binding.etEmail.getText().toString();
        String contact = binding.etContact.getText().toString();

        if (name.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Booking booking = new Booking();
        booking.carId = car.id;
        booking.customerName = name;
        booking.customerEmail = email;
        booking.customerPhone = contact;
        booking.totalAmount = car.pricePerDay;
        booking.status = "PENDING";
        booking.withDriver = binding.switchDriver.isChecked();

        RetrofitClient.getService().createBooking(booking).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookingActivity.this, "Booking Successful!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(BookingActivity.this, "Booking Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}