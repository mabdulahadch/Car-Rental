package com.example.carrental.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.carrental.MainActivity;
import com.example.carrental.R;
import com.example.carrental.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MaterialButton btnAction;
    private SessionManager sessionManager;
    private List<OnboardingItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        if (!sessionManager.isFirstTimeLaunch()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.vp_onboarding);
        btnAction = findViewById(R.id.btn_action);

        setupItems();
        viewPager.setAdapter(new OnboardingAdapter(items));

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == items.size() - 1) {
                    btnAction.setText("Get Started");
                } else {
                    btnAction.setText("Next");
                }
            }
        });

        btnAction.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < items.size() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                sessionManager.setFirstTimeLaunch(false);
                startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void setupItems() {
        items = new ArrayList<>();
        items.add(new OnboardingItem(R.drawable.logo, "Find Your Dream Car", "Explore top-tier vehicles available for rent at your convenience."));
        items.add(new OnboardingItem(R.drawable.compass, "Navigate Anywhere", "Get specific location intents and seamless map experiences directly in app."));
        items.add(new OnboardingItem(R.drawable.user, "Become A Partner", "If you own a showroom, join our network and host cars to the public easily."));
    }

    static class OnboardingItem {
        int imageRes;
        String title, desc;
        OnboardingItem(int img, String t, String d) { imageRes = img; title = t; desc = d; }
    }

    static class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.Holder> {
        List<OnboardingItem> items;
        OnboardingAdapter(List<OnboardingItem> i) { items = i; }

        @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding, parent, false));
        }
        @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
            OnboardingItem item = items.get(position);
            holder.iv.setImageResource(item.imageRes);
            holder.tt.setText(item.title);
            holder.td.setText(item.desc);
        }
        @Override public int getItemCount() { return items.size(); }
        static class Holder extends RecyclerView.ViewHolder {
            ImageView iv; TextView tt, td;
            Holder(View v) { super(v); iv=v.findViewById(R.id.iv_onboarding); tt=v.findViewById(R.id.tv_title); td=v.findViewById(R.id.tv_desc); }
        }
    }
}
