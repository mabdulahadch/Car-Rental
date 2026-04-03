package com.example.carrental.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.carrental.R;
import com.example.carrental.adapters.ImageSliderAdapter;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.databinding.ActivityCarDetailBinding;
import com.example.carrental.models.Car;
import com.google.android.material.tabs.TabLayoutMediator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDetailActivity extends AppCompatActivity {

    private ActivityCarDetailBinding binding;
    private String carId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCarDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        carId = getIntent().getStringExtra("CAR_ID");
        if (carId == null) {
            finish();
            return;
        }

        setupButtons();
        fetchCarDetails();
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void fetchCarDetails() {
        showLoading();
        RetrofitClient.getCarApiService().getCarDetail(carId).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    displayCarDetails(response.body());
                } else {
                    Toast.makeText(CarDetailActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                hideLoading();
                Log.e("CarDetailActivity", "Error: " + t.getMessage());
                Toast.makeText(CarDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        binding.layoutLoading.loadingView.setVisibility(android.view.View.VISIBLE);
        
        android.view.animation.RotateAnimation rotate = new android.view.animation.RotateAnimation(
                0, 360,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(1000);
        rotate.setRepeatCount(android.view.animation.Animation.INFINITE);
        binding.layoutLoading.ivLoadingLogo.startAnimation(rotate);
    }

    private void hideLoading() {
        binding.layoutLoading.loadingView.setVisibility(android.view.View.GONE);
        binding.layoutLoading.ivLoadingLogo.clearAnimation();
    }

    private void displayCarDetails(Car car) {
        binding.tvDetailCarName.setText(car.getBrand() + " " + car.getModel());
        binding.tvDetailDescription.setText(car.getDescription() != null ? car.getDescription() : "No description available.");
        binding.tvDetailRating.setText(String.valueOf(car.getRating()));
        binding.tvDetailPrice.setText("PKR " + (int)car.getPriceperday() + "/Day");
        binding.tvDetailSeats.setText(car.getSeats() + " Seats");
        binding.tvDetailEngine.setText(car.getEnginepower() != null ? car.getEnginepower() : "N/A");
        binding.tvDetailMaxSpeed.setText(car.getMaxspeed() != null ? car.getMaxspeed() : "N/A");

        if (car.getShowroom() != null) {
            binding.tvOwnerName.setText(car.getShowroom().getName());
            String ownerImageUrl = car.getShowroom().getImageUrl();
            if (ownerImageUrl != null) {
                Glide.with(this)
                        .load(ownerImageUrl)
                        .placeholder(R.drawable.user)
                        .circleCrop()
                        .into(binding.ivOwner);
            }
        }

        if (car.getImages() != null && !car.getImages().isEmpty()) {
            ImageSliderAdapter adapter = new ImageSliderAdapter(car.getImages());
            binding.vpCarImages.setAdapter(adapter);
            new TabLayoutMediator(binding.tabLayoutDots, binding.vpCarImages, (tab, position) -> {}).attach();
        }
    }
}
