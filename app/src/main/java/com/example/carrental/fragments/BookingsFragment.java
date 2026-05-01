package com.example.carrental.fragments;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carrental.adapters.BookingAdapter;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.databinding.FragmentBookingsBinding;
import com.example.carrental.models.Booking;
import com.example.carrental.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingsFragment extends Fragment implements BookingAdapter.OnCancelClickListener {
    private FragmentBookingsBinding binding;
    private BookingAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        adapter = new BookingAdapter(bookingList, this);
        binding.rvBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBookings.setAdapter(adapter);

        binding.swipeRefreshBookings.setOnRefreshListener(() -> {
            binding.swipeRefreshBookings.setRefreshing(false);
            loadBookings();
        });

        loadBookings();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookings();
    }

    private void loadBookings() {
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            showEmptyState();
            return;
        }

        showLoading();

        RetrofitClient.getCarApiService().getUserBookings(userId).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    bookingList.clear();
                    bookingList.addAll(response.body());
                    adapter.setBookings(bookingList);

                    if (bookingList.isEmpty()) {
                        showEmptyState();
                    } else {
                        hideEmptyState();
                    }
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                hideLoading();
                Log.e("BookingsFragment", "Error loading bookings", t);
                showEmptyState();
                Toast.makeText(getContext(), "Failed to load bookings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancelClick(Booking booking, int position) {
        // Update status to CANCELLED
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("status", "CANCELLED");

        RetrofitClient.getCarApiService().updateBookingStatus(booking.getId(), statusMap).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                    loadBookings(); // Refresh the list
                } else {
                    Toast.makeText(getContext(), "Failed to cancel booking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        binding.rvBookings.setVisibility(View.GONE);
        binding.llNoBookings.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        binding.rvBookings.setVisibility(View.VISIBLE);
        binding.llNoBookings.setVisibility(View.GONE);
    }

    private void showLoading() {
        binding.layoutLoading.loadingView.setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}