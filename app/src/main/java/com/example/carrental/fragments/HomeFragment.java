package com.example.carrental.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.carrental.CarDetailsActivity;
import com.example.carrental.adapters.BrandAdapter;
import com.example.carrental.adapters.CarAdapter;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.databinding.FragmentHomeBinding;
import com.example.carrental.models.Car;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private CarAdapter bestCarsAdapter;
    private CarAdapter nearbyCarsAdapter;
    private BrandAdapter brandAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerViews();
        fetchData();
    }

    private void setupRecyclerViews() {
        List<String> brands = Arrays.asList("All", "Tesla", "Lamborghini", "BMW", "Ferrari", "Mercedes");
        brandAdapter = new BrandAdapter(brands, brand -> fetchData(brand.equals("All") ? null : brand));
        binding.rvBrands.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvBrands.setAdapter(brandAdapter);

        bestCarsAdapter = new CarAdapter(new ArrayList<>(), this::openCarDetails);
        binding.rvBestCars.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvBestCars.setAdapter(bestCarsAdapter);

        nearbyCarsAdapter = new CarAdapter(new ArrayList<>(), this::openCarDetails);
        binding.rvNearbyCars.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvNearbyCars.setAdapter(nearbyCarsAdapter);
    }

    private void fetchData() {
        fetchData(null);
    }

    private void fetchData(String brandFilter) {
        RetrofitClient.getService().getCars(brandFilter, null).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bestCarsAdapter = new CarAdapter(response.body(), HomeFragment.this::openCarDetails);
                    binding.rvBestCars.setAdapter(bestCarsAdapter);
                    nearbyCarsAdapter = new CarAdapter(response.body(), HomeFragment.this::openCarDetails);
                    binding.rvNearbyCars.setAdapter(nearbyCarsAdapter);
                } else {
                    Log.e("API_ERROR", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Log.e("API_ERROR", "Failed: " + t.getMessage());
            }
        });
    }

    private void openCarDetails(Car car) {
        Intent intent = new Intent(getActivity(), CarDetailsActivity.class);
        intent.putExtra("car_json", new Gson().toJson(car));
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}