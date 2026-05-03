package com.example.carrental.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carrental.R;
import com.example.carrental.activities.BecomePartnerActivity;
import com.example.carrental.activities.FavoriteCarsActivity;
import com.example.carrental.activities.LoginActivity;
import com.example.carrental.partner.PartnerDashboardActivity;
import com.example.carrental.partner.PartnerLoginActivity;
import com.example.carrental.utils.SessionManager;

public class ProfileFragment extends Fragment {
    
    private SessionManager sessionManager;

    private View cardBecomePartner, cardPartnerLogin, cardPartnerDashboard;
    private TextView tvName, tvEmail, tvAuthAction;
    private ImageView ivAuthIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sessionManager = new SessionManager(requireContext());

        tvName = view.findViewById(R.id.tv_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvAuthAction = view.findViewById(R.id.tv_auth_action);
        ivAuthIcon = view.findViewById(R.id.iv_auth_icon);
        
        LinearLayout rowAuthAction = view.findViewById(R.id.row_auth_action);
        LinearLayout rowFavCars = view.findViewById(R.id.row_fav_cars);
        
        cardBecomePartner = view.findViewById(R.id.card_become_partner);
        cardPartnerLogin = view.findViewById(R.id.card_partner_login);
        cardPartnerDashboard = view.findViewById(R.id.card_partner_dashboard);

        View btnBecomePartner = view.findViewById(R.id.btn_become_partner);
        View btnPartnerLogin = view.findViewById(R.id.btn_partner_login);
        View btnPartnerDashboard = view.findViewById(R.id.btn_partner_dashboard);

        rowAuthAction.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                sessionManager.logoutUser();
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                // Refresh fragment
                getParentFragmentManager().beginTransaction().detach(this).attach(this).commit();
            } else {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
        });

        rowFavCars.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), FavoriteCarsActivity.class));
        });

        btnBecomePartner.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), BecomePartnerActivity.class));
        });

        btnPartnerLogin.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), PartnerLoginActivity.class));
        });

        btnPartnerDashboard.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), PartnerDashboardActivity.class));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (sessionManager.isLoggedIn()) {
            tvName.setText(sessionManager.getUserName() != null ? sessionManager.getUserName() : "User");
            tvEmail.setText(sessionManager.getUserEmail() != null ? sessionManager.getUserEmail() : "user@app.com");
            tvAuthAction.setText("Log out");
        } else {
            tvName.setText("Guest User");
            tvEmail.setText("Please login or become a partner");
            tvAuthAction.setText("Log In");
        }

        if (sessionManager.isPartner()) {
            cardBecomePartner.setVisibility(View.GONE);
            cardPartnerLogin.setVisibility(View.GONE);
            cardPartnerDashboard.setVisibility(View.VISIBLE);
        } else {
            cardBecomePartner.setVisibility(View.VISIBLE);
            cardPartnerLogin.setVisibility(View.VISIBLE);
            cardPartnerDashboard.setVisibility(View.GONE);
        }
    }
}