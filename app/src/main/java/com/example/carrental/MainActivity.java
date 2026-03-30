package com.example.carrental;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.carrental.adapters.BrandAdapter;
import com.example.carrental.adapters.CarAdapter;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.databinding.ActivityMainBinding;
import com.example.carrental.models.Car;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CarAdapter bestCarsAdapter;
    private CarAdapter nearbyCarsAdapter;
    private BrandAdapter brandAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerViews();
        setupNavigation();
        fetchData();
        
        binding.btnFilter.setOnClickListener(v -> {
            startActivity(new Intent(this, FilterActivity.class));
        });
        
        binding.ivNotificationHeader.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        binding.ivProfileHeader.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    private void setupNavigation() {
        updateNavIcons(binding.navHome);

        binding.navHome.setOnClickListener(v -> {
            updateNavIcons(binding.navHome);
            binding.homeContent.setVisibility(View.VISIBLE);
        });

        binding.navSearch.setOnClickListener(v -> {
            updateNavIcons(binding.navSearch);
            Toast.makeText(this, "Search coming soon", Toast.LENGTH_SHORT).show();
        });

        binding.navNotifications.setOnClickListener(v -> {
            updateNavIcons(binding.navNotifications);
            startActivity(new Intent(this, NotificationActivity.class));
        });

        binding.navProfile.setOnClickListener(v -> {
            updateNavIcons(binding.navProfile);
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    private void updateNavIcons(ImageView activeIcon) {
        int grayColor = getResources().getColor(R.color.gray_text);
        int whiteColor = getResources().getColor(R.color.onPrimary);

        binding.navHome.setColorFilter(grayColor);
        binding.navSearch.setColorFilter(grayColor);
        binding.navMessages.setColorFilter(grayColor);
        binding.navNotifications.setColorFilter(grayColor);
        binding.navProfile.setColorFilter(grayColor);

        activeIcon.setColorFilter(whiteColor);
    }

    private void setupRecyclerViews() {
        List<String> brands = Arrays.asList("All", "Tesla", "Lamborghini", "BMW", "Ferrari", "Mercedes");
        brandAdapter = new BrandAdapter(brands, brand -> {
            fetchData(brand.equals("All") ? null : brand);
        });
        binding.rvBrands.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvBrands.setAdapter(brandAdapter);

        bestCarsAdapter = new CarAdapter(new ArrayList<>(), this::openCarDetails);
        binding.rvBestCars.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvBestCars.setAdapter(bestCarsAdapter);

        nearbyCarsAdapter = new CarAdapter(new ArrayList<>(), this::openCarDetails);
        binding.rvNearbyCars.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvNearbyCars.setAdapter(nearbyCarsAdapter);
    }

    private void fetchData() {
        fetchData(null);
    }

    private void fetchData(String brandFilter) {
        String filter = null;
        if (brandFilter != null) {
            filter = "eq." + brandFilter;
        }
        
        RetrofitClient.getService().getCars(filter, null).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful()) {
                    List<Car> cars = response.body();
                    if (cars != null && !cars.isEmpty()) {
                        updateCarLists(cars);
                    } else {
                        Log.d("API_DEBUG", "Response successful but list is empty. Check RLS policies.");
                        Toast.makeText(MainActivity.this, "No cars found in database", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API_DEBUG", "Error Code: " + response.code() + " Message: " + response.message());
                    Toast.makeText(MainActivity.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Log.e("API_DEBUG", "Connection Failed", t);
                Toast.makeText(MainActivity.this, "Network Error. Check URL/Key.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCarLists(List<Car> cars) {
        bestCarsAdapter = new CarAdapter(cars, this::openCarDetails);
        binding.rvBestCars.setAdapter(bestCarsAdapter);
        
        nearbyCarsAdapter = new CarAdapter(cars, this::openCarDetails);
        binding.rvNearbyCars.setAdapter(nearbyCarsAdapter);
    }

    private void openCarDetails(Car car) {
        Intent intent = new Intent(this, CarDetailsActivity.class);
        intent.putExtra("car_json", new Gson().toJson(car));
        startActivity(intent);
    }
}