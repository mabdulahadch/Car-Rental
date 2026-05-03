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

public class PartnerDashboardActivity extends AppCompatActivity implements ShowroomCarAdapter.OnCarActionListener, BookingAdapter.OnCancelClickListener {

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
        tvBookingsHeader = findViewById(R.id.tv_bookings_header);
        rvBookings = findViewById(R.id.rv_partner_bookings);

        // Setup bookings RecyclerView
        bookingAdapter = new BookingAdapter(bookingList, this);
        bookingAdapter.setPartnerView(true);
        if (rvBookings != null) {
            rvBookings.setLayoutManager(new LinearLayoutManager(this));
            rvBookings.setNestedScrollingEnabled(false);
            rvBookings.setAdapter(bookingAdapter);
        }

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
                        // Hide bookings section when no cars
                        if (tvBookingsHeader != null) tvBookingsHeader.setVisibility(View.GONE);
                        if (rvBookings != null) rvBookings.setVisibility(View.GONE);
                    } else {
                        llEmptyState.setVisibility(View.GONE);
                        rvShowroomCars.setVisibility(View.VISIBLE);

                        carAdapter = new ShowroomCarAdapter(PartnerDashboardActivity.this, carList);
                        carAdapter.setOnCarActionListener(PartnerDashboardActivity.this);
                        rvShowroomCars.setLayoutManager(new LinearLayoutManager(PartnerDashboardActivity.this));
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
        String showroomId = sessionManager.getShowroomId();
        if (showroomId == null) return;

        RetrofitClient.getCarApiService().getShowroomBookings(showroomId).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookingList = response.body();
                    updateBookingUI();
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Toast.makeText(PartnerDashboardActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBookingUI() {
        if (tvTotalBookings != null) {
            tvTotalBookings.setText(String.valueOf(bookingList.size()));
        }

        if (bookingAdapter != null) {
            bookingAdapter.setBookings(bookingList);
        }

        // Show/hide bookings section
        if (tvBookingsHeader != null) {
            tvBookingsHeader.setVisibility(bookingList.isEmpty() ? View.GONE : View.VISIBLE);
        }
        if (rvBookings != null) {
            rvBookings.setVisibility(bookingList.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onCancelClick(Booking booking, int position) {
        // For partner: update booking status (approve/reject)
        new AlertDialog.Builder(this)
                .setTitle("Manage Booking")
                .setMessage("What would you like to do with this booking?")
                .setPositiveButton("Approve", (dialog, which) -> {
                    updateBookingStatus(booking, "APPROVED");
                })
                .setNegativeButton("Reject", (dialog, which) -> {
                    updateBookingStatus(booking, "REJECTED");
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void updateBookingStatus(Booking booking, String status) {
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("status", status);

        RetrofitClient.getCarApiService().updateBookingStatus(booking.getId(), statusMap).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PartnerDashboardActivity.this, "Booking " + status.toLowerCase(), Toast.LENGTH_SHORT).show();
                    loadAllBookings(); // Refresh
                } else {
                    Toast.makeText(PartnerDashboardActivity.this, "Failed to update booking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(PartnerDashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
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
