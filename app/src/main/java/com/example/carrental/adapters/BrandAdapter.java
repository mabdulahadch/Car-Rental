package com.example.carrental.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carrental.databinding.ItemBrandBinding;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {
    private List<String> brands;
    private OnBrandClickListener listener;

    public interface OnBrandClickListener {
        void onBrandClick(String brand);
    }

    public BrandAdapter(List<String> brands, OnBrandClickListener listener) {
        this.brands = brands;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBrandBinding binding = ItemBrandBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BrandViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        String brand = brands.get(position);
        holder.binding.tvBrandName.setText(brand);
        // Set brand logo based on name (placeholder logic)
        // holder.binding.ivBrandLogo.setImageResource(...);
        holder.itemView.setOnClickListener(v -> listener.onBrandClick(brand));
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    public static class BrandViewHolder extends RecyclerView.ViewHolder {
        ItemBrandBinding binding;
        public BrandViewHolder(ItemBrandBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}