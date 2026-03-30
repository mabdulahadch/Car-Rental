package com.example.carrental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.carrental.databinding.ActivityCarDetailsBinding;
import com.example.carrental.models.Car;
import com.google.gson.Gson;

public class CarDetailsActivity extends AppCompatActivity {
    private ActivityCarDetailsBinding binding;
    private Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCarDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String carJson = getIntent().getStringExtra("car_json");
        if (carJson != null) {
            car = new Gson().fromJson(carJson, Car.class);
            displayCarDetails();
        }

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("car_json", carJson);
            startActivity(intent);
        });
    }

    private void displayCarDetails() {
        binding.tvCarName.setText(car.brand + " " + car.model);
        binding.tvDescription.setText(car.description);
        binding.tvRating.setText(String.valueOf(car.rating));
        binding.tvReviewCount.setText("(" + car.reviewCount + "+ Reviews)");

        if (car.images != null && !car.images.isEmpty()) {
            Glide.with(this).load(car.images.get(0)).into(binding.ivCarImage);
        }
        
        // Populate features and showroom info (mocked or from API)
    }
}