package com.example.carrental.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carrental.R;
import com.example.carrental.databinding.ItemBrandBinding;
import com.example.carrental.models.Brand;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private List<Brand> brandList;
    private OnBrandClickListener listener;

    public interface OnBrandClickListener {
        void onBrandClick(Brand brand);
    }

    public BrandAdapter(List<Brand> brandList, OnBrandClickListener listener) {
        this.brandList = brandList;
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
        Brand brand = brandList.get(position);
        holder.binding.tvBrandName.setText(brand.getName());
        
//        if (brand.getIconRes() != 0) {
//            holder.binding.ivBrandLogo.setVisibility(View.VISIBLE);
//            holder.binding.ivBrandLogo.setImageResource(brand.getIconRes());
//        } else {
//            holder.binding.ivBrandLogo.setVisibility(View.GONE);
//        }

        if (brand.isSelected()) {
            holder.binding.getRoot().getBackground().setTint(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
            holder.binding.tvBrandName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
//            holder.binding.ivBrandLogo.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else {
            holder.binding.getRoot().getBackground().setTint(ContextCompat.getColor(holder.itemView.getContext(), R.color.surface));
            holder.binding.tvBrandName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.onBackground));
//            holder.binding.ivBrandLogo.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.onBackground));
        }

        holder.itemView.setOnClickListener(v -> {
            for (Brand b : brandList) b.setSelected(false);
            brand.setSelected(true);
            notifyDataSetChanged();
            if (listener != null) listener.onBrandClick(brand);
        });
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public void resetSelection() {
        for (int i = 0; i < brandList.size(); i++) {
            brandList.get(i).setSelected(i == 0);
        }
        notifyDataSetChanged();
    }

    public static class BrandViewHolder extends RecyclerView.ViewHolder {
        ItemBrandBinding binding;
        public BrandViewHolder(ItemBrandBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}