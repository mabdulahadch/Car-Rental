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
    private com.example.carrental.utils.SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCarDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new com.example.carrental.utils.SessionManager(this);

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
        
        updateFavoriteIcon();
        binding.btnFav.setOnClickListener(v -> {
            if (sessionManager.isFavorite(carId)) {
                sessionManager.removeFavorite(carId);
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                sessionManager.addFavorite(carId);
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
            updateFavoriteIcon();
        });
    }

    private void updateFavoriteIcon() {
        if (sessionManager.isFavorite(carId)) {
            binding.btnFav.setColorFilter(android.graphics.Color.RED);
        } else {
            binding.btnFav.clearColorFilter();
        }
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
        binding.tvDetailEngine.setText(car.getEnginePower() != null ? car.getEnginePower() : "N/A");
        binding.tvDetailMaxSpeed.setText(car.getMaxSpeed() != null ? car.getMaxSpeed() : "N/A");
        binding.tvFuelType.setText(car.getFuelType() != null ? car.getFuelType() : "N/A");
        binding.tvColorType.setText(car.getColor() != null ? car.getColor() : "N/A");


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

        binding.btnLocation.setOnClickListener(v -> {
            if (car.getLocation() != null && !car.getLocation().isEmpty()) {
                android.net.Uri gmmIntentUri = android.net.Uri.parse("geo:0,0?q=" + android.net.Uri.encode(car.getLocation()));
                android.content.Intent mapIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Fallback to generic geo intent
                    android.content.Intent genericIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri);
                    startActivity(genericIntent);
                }
            } else {
                Toast.makeText(CarDetailActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
