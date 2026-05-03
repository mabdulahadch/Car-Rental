package com.example.carrental.partner;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carrental.R;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.models.Car;
import com.example.carrental.models.CarCreateRequest;
import com.example.carrental.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewCarActivity extends AppCompatActivity {

    private AutoCompleteTextView etBrand, etModel;
    private TextInputEditText etCategory, etPrice, etFuel, etSeats, etPower, etSpeed, etLocation, etDesc;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_car);

        sessionManager = new SessionManager(this);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        etBrand = findViewById(R.id.et_brand);
        etModel = findViewById(R.id.et_model);
        etCategory = findViewById(R.id.et_category);
        
        // Setup dropdowns
        setupDropdowns();

        etPrice = findViewById(R.id.et_price);
        etFuel = findViewById(R.id.et_fuel);
        etSeats = findViewById(R.id.et_seats);
        etPower = findViewById(R.id.et_power);
        etSpeed = findViewById(R.id.et_speed);
        etLocation = findViewById(R.id.et_location);
        etDesc = findViewById(R.id.et_desc);

        findViewById(R.id.btn_save_car).setOnClickListener(v -> saveCar());
    }

    private void setupDropdowns() {
        // Brands Array
        String[] brands = new String[]{"BMW", "Honda", "Toyota", "Tesla", "Audi", "Ford", "Mercedes-Benz", "Nissan"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, brands);
        etBrand.setAdapter(brandAdapter);

        // Models Array (2000 to 2026)
        String[] models = new String[27];
        for (int i = 0; i < 27; i++) {
            models[i] = String.valueOf(2000 + i);
        }
        ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, models);
        etModel.setAdapter(modelAdapter);
    }

    private void saveCar() {
        String brand = etBrand.getText().toString();
        String model = etModel.getText().toString();
        String priceStr = etPrice.getText().toString();
        
        if (brand.isEmpty() || model.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Brand, Model, and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        String category = etCategory.getText().toString();
        String fuel = etFuel.getText().toString();
        String seatsStr = etSeats.getText().toString();
        int seats = seatsStr.isEmpty() ? 5 : Integer.parseInt(seatsStr);
        String power = etPower.getText().toString();
        String speed = etSpeed.getText().toString();
        String location = etLocation.getText().toString();
        String desc = etDesc.getText().toString();

        String showroomId = sessionManager.getShowroomId();
        
        if (showroomId == null || showroomId.isEmpty()) {
            Toast.makeText(this, "Showroom session error. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        CarCreateRequest request = new CarCreateRequest(
                brand, model, category, price, seats, power, speed, fuel, "Black",
                desc, new ArrayList<>(), "", new ArrayList<>(), showroomId, location,
                true, 5.0, 0
        );

        RetrofitClient.getCarApiService().createCar(request).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddNewCarActivity.this, "Car saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Failed to save car";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(AddNewCarActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                Toast.makeText(AddNewCarActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
