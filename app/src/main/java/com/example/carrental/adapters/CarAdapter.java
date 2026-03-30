package com.example.carrental.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carrental.databinding.ItemCarCardBinding;
import com.example.carrental.models.Car;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private List<Car> cars;
    private OnCarClickListener listener;

    public interface OnCarClickListener {
        void onCarClick(Car car);
    }

    public CarAdapter(List<Car> cars, OnCarClickListener listener) {
        this.cars = cars;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCarCardBinding binding = ItemCarCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = cars.get(position);
        holder.binding.tvCarName.setText(car.brand + " " + car.model);
        holder.binding.tvPrice.setText("$" + (int)car.pricePerDay + "/Day");
        holder.binding.tvSeats.setText(car.seats + " Seats");
        holder.binding.tvLocation.setText(car.location);
        holder.binding.tvRating.setText(String.valueOf(car.rating));

        if (car.images != null && !car.images.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(car.images.get(0))
                    .into(holder.binding.ivCarImage);
        }

        holder.itemView.setOnClickListener(v -> listener.onCarClick(car));
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ItemCarCardBinding binding;
        public CarViewHolder(ItemCarCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}