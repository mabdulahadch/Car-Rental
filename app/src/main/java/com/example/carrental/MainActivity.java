package com.example.carrental;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.carrental.adapters.MainViewPagerAdapter;
import com.example.carrental.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewPager();
        setupBottomNavigation();
    }

    private void setupViewPager() {
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        binding.bottomNavigation.setSelectedItemId(R.id.nav_search);
                        break;
                    case 2:
                        binding.bottomNavigation.setSelectedItemId(R.id.nav_bookings);
                        break;
                    case 3:
                        binding.bottomNavigation.setSelectedItemId(R.id.nav_profile);
                        break;
                }
            }
        });
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                binding.viewPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.nav_search) {
                binding.viewPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.nav_bookings) {
                binding.viewPager.setCurrentItem(2);
                return true;
            } else if (itemId == R.id.nav_profile) {
                binding.viewPager.setCurrentItem(3);
                return true;
            }
            return false;
        });
    }
}