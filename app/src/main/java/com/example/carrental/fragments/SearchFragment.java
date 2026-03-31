package com.example.carrental.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.carrental.R;
import com.example.carrental.adapters.BrandAdapter;
import com.example.carrental.adapters.CarAdapter;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.databinding.FragmentSearchBinding;
import com.example.carrental.models.Brand;
import com.example.carrental.models.Car;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private CarAdapter carAdapter;
    private BrandAdapter brandAdapter;
    private List<Car> allCars = new ArrayList<>();
    private String selectedBrand = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupBrandRecyclerView();
        setupCarRecyclerView();
        setupSearchInput();
        fetchCars();
    }

    private void setupSearchInput() {
        binding.etSearchPage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupBrandRecyclerView() {
        List<Brand> brandList = new ArrayList<>();
        brandList.add(new Brand("All", 0, true));
        brandList.add(new Brand("Honda", R.drawable.logo, false));
        brandList.add(new Brand("Toyota", R.drawable.logo, false));
        brandList.add(new Brand("Suzuki", R.drawable.logo, false));

        brandAdapter = new BrandAdapter(brandList, brand -> {
            selectedBrand = brand.getName();
            applyFilters();
        });
        binding.rvBrandsFilter.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvBrandsFilter.setAdapter(brandAdapter);
    }

    private void setupCarRecyclerView() {
        carAdapter = new CarAdapter(new ArrayList<>());
        binding.rvCarsSearch.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvCarsSearch.setAdapter(carAdapter);
    }

    private void fetchCars() {
        RetrofitClient.getCarApiService().getCars().enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCars = response.body();
                    applyFilters();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch cars", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Log.e("SearchFragment", "Error fetching cars", t);
                Toast.makeText(getContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        String query = binding.etSearchPage.getText().toString().toLowerCase().trim();
        List<Car> filteredList = new ArrayList<>();

        for (Car car : allCars) {
            boolean matchesBrand = selectedBrand.equalsIgnoreCase("All") || car.getBrand().equalsIgnoreCase(selectedBrand);
            boolean matchesQuery = query.isEmpty() || 
                                 car.getBrand().toLowerCase().contains(query) || 
                                 car.getModel().toLowerCase().contains(query);

            if (matchesBrand && matchesQuery) {
                filteredList.add(car);
            }
        }
        carAdapter.setCars(filteredList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}