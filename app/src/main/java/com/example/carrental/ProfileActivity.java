package com.example.carrental;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrental.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        
        // Setup placeholder items
        binding.menuFavorites.tvMenuTitle.setText("Favorite Cars");
        binding.menuPreviousRent.tvMenuTitle.setText("Previous Rant");
        binding.menuNotifications.tvMenuTitle.setText("Notification");
        binding.menuPartnerships.tvMenuTitle.setText("Connected to QENT Partnerships");
        
        binding.menuSettings.tvMenuTitle.setText("Settings");
        binding.menuLanguages.tvMenuTitle.setText("Languages");
        binding.menuInvite.tvMenuTitle.setText("Invite Friends");
        binding.menuPrivacy.tvMenuTitle.setText("privacy policy");
        binding.menuHelp.tvMenuTitle.setText("Help Support");
        binding.menuLogout.tvMenuTitle.setText("Log out");
    }
}