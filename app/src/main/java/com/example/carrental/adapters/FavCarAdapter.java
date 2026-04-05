package com.example.carrental.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carrental.R;
import com.example.carrental.activities.CarDetailActivity;
import com.example.carrental.databinding.ItemFavCarBinding;
import com.example.carrental.models.Car;

import java.util.List;

public class FavCarAdapter extends RecyclerView.Adapter<FavCarAdapter.FavCarViewHolder> {

    private List<Car> carList;

    public FavCarAdapter(List<Car> carList) {
        this.carList = carList;
    }

    @NonNull
    @Override
    public FavCarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavCarBinding binding = ItemFavCarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FavCarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavCarViewHolder holder, int position) {
        Car car = carList.get(position);

        holder.binding.tvCarName.setText(car.getBrand() + " " + car.getModel());
        holder.binding.tvPrice.setText("PKR " + (int) car.getPriceperday() + "/Day");
        holder.binding.tvLocation.setText(car.getLocation());

        if (car.getImages() != null && !car.getImages().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(car.getImages().get(0))
                    .placeholder(R.drawable.car_image)
                    .into(holder.binding.ivCar);
        } else {
            holder.binding.ivCar.setImageResource(R.drawable.car_image);
        }

        // Whole card click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CarDetailActivity.class);
            intent.putExtra("CAR_ID", car.getId());
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return carList != null ? carList.size() : 0;
    }

    public void setCars(List<Car> cars) {
        this.carList = cars;
        notifyDataSetChanged();
    }

    public static class FavCarViewHolder extends RecyclerView.ViewHolder {
        ItemFavCarBinding binding;

        public FavCarViewHolder(ItemFavCarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
