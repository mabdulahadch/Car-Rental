package com.example.carrental.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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

public class SearchFragment extends Fragment implements FilterBottomSheet.FilterListener {
    private FragmentSearchBinding binding;
    private CarAdapter carAdapter;
    private BrandAdapter brandAdapter;
    private List<Car> allCars = new ArrayList<>();
    private String selectedBrand = "All";

    // Filter state from bottom sheet
    private String filterCategory = "All";
    private float filterMinPrice = 0;
    private float filterMaxPrice = 100000;
    private String filterColor = null;
    private int filterSeats = -1;
    private String filterFuelType = null;

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
        setupSwipeRefresh();
        setupFilterButton();
        fetchCars();
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(false);
            resetFilters();
            fetchCars();
        });
    }

    private void resetFilters() {
        filterCategory = "All";
        filterMinPrice = 0;
        filterMaxPrice = 100000;
        filterColor = null;
        filterSeats = -1;
        filterFuelType = null;
        selectedBrand = "All";
        if (brandAdapter != null) brandAdapter.resetSelection();
        binding.etSearchPage.setText(""); // clear search query too
    }

    private void setupFilterButton() {
        binding.btnFilter.setOnClickListener(v -> {
            FilterBottomSheet filterSheet = new FilterBottomSheet();
            filterSheet.setFilterListener(this);
            filterSheet.setInitialState(filterCategory, filterMinPrice, filterMaxPrice,
                    filterColor, filterSeats, filterFuelType);
            filterSheet.show(getChildFragmentManager(), "FilterBottomSheet");
        });
    }

    @Override
    public void onFiltersApplied(String category, float minPrice, float maxPrice,
                                 String color, int seats, String fuelType) {
        this.filterCategory = category;
        this.filterMinPrice = minPrice;
        this.filterMaxPrice = maxPrice;
        this.filterColor = color;
        this.filterSeats = seats;
        this.filterFuelType = fuelType;
        applyFilters();
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
        showLoading();
        RetrofitClient.getCarApiService().getCars().enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    allCars = response.body();
                    applyFilters();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch cars", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                hideLoading();
                Log.e("SearchFragment", "Error fetching cars", t);
                Toast.makeText(getContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        binding.layoutLoading.loadingView.setVisibility(View.VISIBLE);
        
        // Create rotation animation
        RotateAnimation rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE);
        binding.layoutLoading.ivLoadingLogo.startAnimation(rotate);
    }

    private void hideLoading() {
        binding.layoutLoading.loadingView.setVisibility(View.GONE);
        binding.layoutLoading.ivLoadingLogo.clearAnimation();
    }

    private void applyFilters() {
        String query = binding.etSearchPage.getText().toString().toLowerCase().trim();
        List<Car> filteredList = new ArrayList<>();

        for (Car car : allCars) {
            // Brand filter (from horizontal brand chips)
            boolean matchesBrand = selectedBrand.equalsIgnoreCase("All") || car.getBrand().equalsIgnoreCase(selectedBrand);

            // Search query filter
            boolean matchesQuery = query.isEmpty() || 
                                 car.getBrand().toLowerCase().contains(query) || 
                                 car.getModel().toLowerCase().contains(query);

            // Category filter (from bottom sheet)
            boolean matchesCategory = filterCategory.equals("All") ||
                    (filterCategory.equals("SUV") && car.getCategory() != null && car.getCategory().equalsIgnoreCase("SUV")) ||
                    (filterCategory.equals("Sedan") && (car.getCategory() == null || car.getCategory().equalsIgnoreCase("Sedan"))) ||
                    (filterCategory.equals("Hatchback") && (car.getCategory() == null || car.getCategory().equalsIgnoreCase("Hatchback")));

            // Price range filter
            boolean matchesPrice = car.getPriceperday() >= filterMinPrice && car.getPriceperday() <= filterMaxPrice;

            // Color filter
            boolean matchesColor = filterColor == null || 
                    (car.getColor() != null && car.getColor().equalsIgnoreCase(filterColor));

            // Seats filter
            boolean matchesSeats = filterSeats == -1 || car.getSeats() == filterSeats;

            // Fuel type filter
            boolean matchesFuel = filterFuelType == null || 
                    (car.getFuelType() != null && car.getFuelType().equalsIgnoreCase(filterFuelType));

            if (matchesBrand && matchesQuery && matchesCategory && matchesPrice && matchesColor && matchesSeats && matchesFuel) {
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