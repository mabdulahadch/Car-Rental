package com.example.carrental.activities;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrental.R;
import com.example.carrental.adapters.FavCarAdapter;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.databinding.ActivityFavoriteCarsBinding;
import com.example.carrental.models.Car;
import com.example.carrental.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteCarsActivity extends AppCompatActivity {

    private ActivityFavoriteCarsBinding binding;
    private RecyclerView rvFavorites;
    private TextView tvNoFavorites;
    private FavCarAdapter adapter;
    private SessionManager sessionManager;
    private List<Car> favoriteCars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_favorite_cars);

        binding = ActivityFavoriteCarsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        rvFavorites = binding.rvFavoriteCars;
        tvNoFavorites = binding.tvNoFavorites;
        
        binding.btnFavBack.setOnClickListener(v -> finish());

        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        favoriteCars = new ArrayList<>();
        
        adapter = new FavCarAdapter(favoriteCars);
        rvFavorites.setAdapter(adapter);

        loadFavorites();
    }

    private void loadFavorites() {
        showLoading();
        Set<String> favIds = sessionManager.getFavorites();
        if (favIds.isEmpty()) {
            hideLoading();
            tvNoFavorites.setVisibility(View.VISIBLE);
            return;
        }

        RetrofitClient.getCarApiService().getCars().enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    favoriteCars.clear();
                    for (Car c : response.body()) {
                        if (favIds.contains(c.getId())) {
                            favoriteCars.add(c);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    
                    if (favoriteCars.isEmpty()) {
                        tvNoFavorites.setVisibility(View.VISIBLE);
                    } else {
                        tvNoFavorites.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                hideLoading();
                Toast.makeText(FavoriteCarsActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
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
}
