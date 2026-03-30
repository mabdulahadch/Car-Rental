package com.example.carrental;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrental.databinding.ActivityNotificationBinding;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // For now, we show the empty state
        binding.emptyState.setVisibility(View.VISIBLE);
        binding.rvNotifications.setVisibility(View.GONE);
    }
}