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
import com.example.carrental.utils.SessionManager;

public class ProfileFragment extends Fragment {
    
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sessionManager = new SessionManager(requireContext());

        TextView tvName = view.findViewById(R.id.tv_profile_name);
        TextView tvEmail = view.findViewById(R.id.tv_profile_email);
        TextView tvAuthAction = view.findViewById(R.id.tv_auth_action);
        ImageView ivAuthIcon = view.findViewById(R.id.iv_auth_icon);
        LinearLayout rowAuthAction = view.findViewById(R.id.row_auth_action);
        LinearLayout rowFavCars = view.findViewById(R.id.row_fav_cars);
        View btnBecomePartner = view.findViewById(R.id.btn_become_partner);

        if (sessionManager.isLoggedIn()) {
            tvName.setText(sessionManager.getUserName() != null ? sessionManager.getUserName() : "User");
            tvEmail.setText(sessionManager.getUserEmail() != null ? sessionManager.getUserEmail() : "user@app.com");
            tvAuthAction.setText("Log out");
        } else {
            tvName.setText("Guest User");
            tvEmail.setText("Please login or become a partner");
            tvAuthAction.setText("Log In");
//            ivAuthIcon.setImageResource(android.R.drawable.ic_menu_login);
        }

        if (sessionManager.isPartner()) {
            btnBecomePartner.setVisibility(View.GONE);
        }

        rowAuthAction.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                sessionManager.logoutUser();
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                // Refresh fragment
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
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
    }
}