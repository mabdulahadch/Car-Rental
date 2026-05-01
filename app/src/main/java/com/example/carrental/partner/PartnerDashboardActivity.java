package com.example.carrental.partner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrental.R;
import com.example.carrental.adapters.BookingAdapter;
import com.example.carrental.adapters.ShowroomCarAdapter;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.models.Booking;
import com.example.carrental.models.Car;
import com.example.carrental.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartnerDashboardActivity extends AppCompatActivity implements ShowroomCarAdapter.OnCarActionListener {

    private RecyclerView rvShowroomCars, rvBookings;
    private LinearLayout llEmptyState;
    private TextView tvTotalCars, tvTotalBookings, tvBookingsHeader;
    private SessionManager sessionManager;
    private ShowroomCarAdapter carAdapter;
    private BookingAdapter bookingAdapter;
    private List<Car> carList = new ArrayList<>();
    private List<Booking> bookingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_dashboard);

        sessionManager = new SessionManager(this);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        FloatingActionButton fabAddCar = findViewById(R.id.fab_add_car);
        rvShowroomCars = findViewById(R.id.rv_showroom_cars);
        llEmptyState = findViewById(R.id.ll_empty_state);
        tvTotalCars = findViewById(R.id.tv_total_cars);
        tvTotalBookings = findViewById(R.id.tv_total_bookings);

        fabAddCar.setOnClickListener(v -> {
            startActivity(new Intent(PartnerDashboardActivity.this, AddNewCarActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadShowroomCars();
    }

    private void loadShowroomCars() {
        if (sessionManager == null) return;
        String showroomId = sessionManager.getShowroomId();
        if (showroomId == null) return;

        RetrofitClient.getCarApiService().getShowroomCars(showroomId).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    carList = response.body();

                    // Update stats
                    if (tvTotalCars != null) {
                        tvTotalCars.setText(String.valueOf(carList.size()));
                    }

                    if (carList.isEmpty()) {
                        rvShowroomCars.setVisibility(View.GONE);
                        llEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        llEmptyState.setVisibility(View.GONE);
                        rvShowroomCars.setVisibility(View.VISIBLE);

                        carAdapter = new ShowroomCarAdapter(PartnerDashboardActivity.this, carList);
                        carAdapter.setOnCarActionListener(PartnerDashboardActivity.this);
                        rvShowroomCars.setAdapter(carAdapter);

                        // Load bookings for all cars
                        loadAllBookings();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Toast.makeText(PartnerDashboardActivity.this, "Failed to load cars", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllBookings() {
        // Load bookings for each car the partner owns
        bookingList.clear();
        int[] pending = {carList.size()};

        for (Car car : carList) {
            RetrofitClient.getCarApiService().getCarBookings(car.getId()).enqueue(new Callback<List<Booking>>() {
                @Override
                public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        bookingList.addAll(response.body());
                    }
                    pending[0]--;
                    if (pending[0] <= 0) {
                        updateBookingStats();
                    }
                }

                @Override
                public void onFailure(Call<List<Booking>> call, Throwable t) {
                    pending[0]--;
                    if (pending[0] <= 0) {
                        updateBookingStats();
                    }
                }
            });
        }
    }

    private void updateBookingStats() {
        if (tvTotalBookings != null) {
            tvTotalBookings.setText(String.valueOf(bookingList.size()));
        }
    }

    @Override
    public void onDeleteCar(Car car, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Car")
                .setMessage("Are you sure you want to delete " + car.getBrand() + " " + car.getModel() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    RetrofitClient.getCarApiService().deleteCar(car.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                if (carAdapter != null) {
                                    carAdapter.removeCar(position);
                                }
                                if (tvTotalCars != null) {
                                    tvTotalCars.setText(String.valueOf(carList.size()));
                                }
                                Toast.makeText(PartnerDashboardActivity.this, "Car deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PartnerDashboardActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(PartnerDashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
