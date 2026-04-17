package com.example.carrental.partner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carrental.R;
import com.example.carrental.adapters.ShowroomCarAdapter;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.models.Car;
import com.example.carrental.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartnerDashboardActivity extends AppCompatActivity {

    private RecyclerView rvShowroomCars;
    private LinearLayout llEmptyState;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_dashboard);

        sessionManager = new SessionManager(this);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        FloatingActionButton fabAddCar = findViewById(R.id.fab_add_car);
        rvShowroomCars = findViewById(R.id.rv_showroom_cars);
        llEmptyState = findViewById(R.id.ll_empty_state);
        // Find total cars TextView (it's the first static "0" right now, assigning it properly via ID later)
        // For now, let's just use empty state toggle
        
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
                    List<Car> cars = response.body();
                    if (cars.isEmpty()) {
                        rvShowroomCars.setVisibility(View.GONE);
                        llEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        llEmptyState.setVisibility(View.GONE);
                        rvShowroomCars.setVisibility(View.VISIBLE);
                        
                        ShowroomCarAdapter adapter = new ShowroomCarAdapter(PartnerDashboardActivity.this, cars);
                        rvShowroomCars.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Toast.makeText(PartnerDashboardActivity.this, "Failed to load cars", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
