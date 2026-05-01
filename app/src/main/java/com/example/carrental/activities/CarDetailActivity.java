package com.example.carrental.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.carrental.R;
import com.example.carrental.adapters.ImageSliderAdapter;
import com.example.carrental.adapters.ReviewAdapter;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.databinding.ActivityCarDetailBinding;
import com.example.carrental.models.Car;
import com.example.carrental.models.Review;
import com.example.carrental.models.ReviewRequest;
import com.example.carrental.utils.SessionManager;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDetailActivity extends AppCompatActivity {

    private ActivityCarDetailBinding binding;
    private String carId;
    private SessionManager sessionManager;
    private Car currentCar;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCarDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        carId = getIntent().getStringExtra("CAR_ID");
        if (carId == null) {
            finish();
            return;
        }

        setupButtons();
        setupReviewsRecyclerView();
        fetchCarDetails();
        loadReviews();
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

        // Book Now button
        binding.btnBookNow.setOnClickListener(v -> {
            if (currentCar == null) {
                Toast.makeText(this, "Car data not loaded yet", Toast.LENGTH_SHORT).show();
                return;
            }
            android.content.Intent intent = new android.content.Intent(this, BookingActivity.class);
            intent.putExtra("CAR_ID", carId);
            intent.putExtra("CAR_NAME", currentCar.getBrand() + " " + currentCar.getModel());
            intent.putExtra("CAR_PRICE", currentCar.getPriceperday());
            if (currentCar.getImages() != null && !currentCar.getImages().isEmpty()) {
                intent.putExtra("CAR_IMAGE", currentCar.getImages().get(0));
            }
            startActivity(intent);
        });

        // Write Review button
        binding.btnWriteReview.setOnClickListener(v -> showWriteReviewDialog());
    }

    private void setupReviewsRecyclerView() {
        reviewAdapter = new ReviewAdapter(reviewList);
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setNestedScrollingEnabled(false);
        binding.rvReviews.setAdapter(reviewAdapter);
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

    private void loadReviews() {
        RetrofitClient.getCarApiService().getCarReviews(carId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewList.clear();
                    reviewList.addAll(response.body());
                    reviewAdapter.setReviews(reviewList);
                    binding.tvReviewCount.setText(reviewList.size() + " reviews");
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.e("CarDetailActivity", "Error loading reviews: " + t.getMessage());
            }
        });
    }

    private void showWriteReviewDialog() {
        View dialogView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, null);

        // Build a custom dialog with rating bar and comment
        EditText etComment = new EditText(this);
        etComment.setHint("Write your review...");
        etComment.setMinLines(3);
        etComment.setPadding(40, 20, 40, 20);

        // Since we can't easily embed a RatingBar in a simple AlertDialog, we use a number-based approach
        final String[] ratingOptions = {"5 - Excellent", "4 - Very Good", "3 - Good", "2 - Fair", "1 - Poor"};
        final float[] selectedRating = {5.0f};

        new AlertDialog.Builder(this)
                .setTitle("Write a Review")
                .setSingleChoiceItems(ratingOptions, 0, (dialog, which) -> {
                    selectedRating[0] = 5.0f - which;
                })
                .setView(etComment)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String comment = etComment.getText().toString().trim();
                    if (comment.isEmpty()) {
                        Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String userName = sessionManager.getUserName();
                    if (userName == null) userName = "Guest";

                    ReviewRequest request = new ReviewRequest(
                            userName, null, selectedRating[0], comment, carId
                    );

                    RetrofitClient.getCarApiService().createReview(request).enqueue(new Callback<Review>() {
                        @Override
                        public void onResponse(Call<Review> call, Response<Review> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(CarDetailActivity.this, "Review submitted!", Toast.LENGTH_SHORT).show();
                                loadReviews(); // Refresh reviews
                            } else {
                                Toast.makeText(CarDetailActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Review> call, Throwable t) {
                            Toast.makeText(CarDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLoading() {
        binding.layoutLoading.loadingView.setVisibility(View.VISIBLE);

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
        binding.layoutLoading.loadingView.setVisibility(View.GONE);
        binding.layoutLoading.ivLoadingLogo.clearAnimation();
    }

    private void displayCarDetails(Car car) {
        this.currentCar = car;
        binding.tvDetailCarName.setText(car.getBrand() + " " + car.getModel());
        binding.tvDetailDescription.setText(car.getDescription() != null ? car.getDescription() : "No description available.");
        binding.tvDetailRating.setText(String.valueOf(car.getRating()));
        binding.tvDetailPrice.setText("PKR " + (int) car.getPriceperday() + "/Day");
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
            new TabLayoutMediator(binding.tabLayoutDots, binding.vpCarImages, (tab, position) -> {
            }).attach();
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
