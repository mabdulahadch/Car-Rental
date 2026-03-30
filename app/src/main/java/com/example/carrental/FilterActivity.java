package com.example.carrental;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrental.databinding.ActivityFilterBinding;

public class FilterActivity extends AppCompatActivity {
    private ActivityFilterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        
        binding.btnShowResults.setOnClickListener(v -> {
            // Apply filters and return
            finish();
        });

        binding.tvClearAll.setOnClickListener(v -> {
            // Reset filters
        });
    }
}