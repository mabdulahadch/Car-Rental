package com.example.carrental.fragments;

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

import com.example.carrental.R;
import com.example.carrental.adapters.BrandAdapter;
import com.example.carrental.adapters.CarAdapter;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.databinding.FragmentHomeBinding;
import com.example.carrental.models.Brand;
import com.example.carrental.models.Car;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private CarAdapter bestCarsAdapter;
    private CarAdapter nearbyCarsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupBrands();
        setupCarRecyclerViews();
        fetchCars();
    }

    private void setupBrands() {
        List<Brand> brandList = new ArrayList<>();
        brandList.add(new Brand("All", 0, true));
        brandList.add(new Brand("Honda", R.drawable.logo, false));
        brandList.add(new Brand("Toyota", R.drawable.logo, false));
        brandList.add(new Brand("Suzuki", R.drawable.logo, false));
        brandList.add(new Brand("BMW", R.drawable.logo, false));
        brandList.add(new Brand("Tesla", R.drawable.logo, false));

        BrandAdapter brandAdapter = new BrandAdapter(brandList, brand -> {
            // Navigate to search tab with brand filter (optional enhancement)
        });
        binding.rvBrands.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvBrands.setAdapter(brandAdapter);
    }

    private void setupCarRecyclerViews() {
        bestCarsAdapter = new CarAdapter(new ArrayList<>());
        binding.rvBestCars.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvBestCars.setAdapter(bestCarsAdapter);

        nearbyCarsAdapter = new CarAdapter(new ArrayList<>());
        binding.rvNearbyCars.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNearbyCars.setAdapter(nearbyCarsAdapter);
    }

    private void fetchCars() {
        RetrofitClient.getCarApiService().getCars().enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Car> allCars = response.body();

                    // Best Cars: sort by rating descending, take top items
                    List<Car> bestCars = new ArrayList<>(allCars);
                    Collections.sort(bestCars, (a, b) -> Float.compare(b.getRating(), a.getRating()));
                    bestCarsAdapter.setCars(bestCars.size() > 10 ? bestCars.subList(0, 10) : bestCars);

                    // Nearby Cars: just show the rest or shuffled list
                    List<Car> nearbyCars = new ArrayList<>(allCars);
                    Collections.shuffle(nearbyCars);
                    nearbyCarsAdapter.setCars(nearbyCars.size() > 10 ? nearbyCars.subList(0, 10) : nearbyCars);
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching cars", t);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading cars", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}